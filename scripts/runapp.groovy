import com.bleedingwolf.ratpack.RatpackApp
import com.bleedingwolf.ratpack.RatpackServlet
import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.Context
import org.mortbay.jetty.servlet.ServletHolder

public abstract class DirWatcher extends TimerTask {
  def path
  def dir = [:]
  // Exclude temp files created by vim, emacs, etc...
  FileFilter fileFilter = {file -> !(file.name =~ /\.swp$|\~$|^\./)} as FileFilter
  
  public DirWatcher(String path) {
    this.path = path;
    def files = new File(path).listFiles(fileFilter);
    
    // transfer to the hashmap be used a reference and keep the lastModfied value
    for(File file : files) {
      dir.put(file, file.lastModified());
    }
  }
  
  public final void run() {
    def checkedFiles = new HashSet();
    def files = new File(path).listFiles(fileFilter);
    
    // scan the files and check for modification/addition
    for (File file : files) { 
      Long current = dir.get(file)
      checkedFiles.add(file)
      if (current == null) {
        // new file
        dir.put(file, new Long(file.lastModified()))
        onChange(file, "add")
      }
      else if (current.longValue() != file.lastModified()){
        // modified file
        dir.put(file, new Long(file.lastModified()))
        onChange(file, "modify")
      }
    }
    
    // now check for deleted files
    def deletedFiles = dir.clone().keySet() - checkedFiles
    deletedFiles.each {
      dir.remove(it)
      onChange(it, "delete") 
    }
  }
  
  protected abstract void onChange(File file, String action);
}

class AppRunner extends DirWatcher {
  RatpackApp app
  Server server
  File script
    String public_path
  def server_port = System.getProperty('ratpack.port', '0') as Integer

  AppRunner(String script, String path, String public_path = null)
  {
    super(path)
    this.script = new File(path, script)
      this.public_path = public_path
  }

  def manageApp() {
    runApp()
    Timer timer = new Timer()
    timer.schedule(this, new Date(), 1000)
  }

  def runApp() {
      app = new RatpackApp()

      app.prepareScriptForExecutionOnApp(script)

      if (public_path) app.config.public = public_path

      if (server_port) app.config.port = server_port

      // Runs this RatpackApp in a Jetty container
      def servlet = new RatpackServlet()
      servlet.app = app

      app.logger

      app.logger.info('Starting Ratpack app with config:\n{}', app.config)

      server = new Server(app.config.port)
      def root = new Context(server, "/", Context.SESSIONS)
      root.addServlet(new ServletHolder(servlet), "/*")

      server.start()
  }

  def killApp() {
      try {
//          println "Stopping server..."
          server.stop()
          server = null
      } catch (Exception ex) {
          println "server.stop failed!"
          ex.printStackTrace()
      }
  }

  void onChange(File file, String action) {
    println ("File "+ file.name +" action: " + action )
    if (server) {
      println "KILLING"
      killApp()
      println "RELOADING"
    } else {
      println "STARTING" 
    }
    runApp()
  }
}

if (args.length > 1 && args.length < 4) {
  new AppRunner(args[0], args[1], (args.length > 2) ? args[2] : null).manageApp()
} else {
  println "Usage:"
  println "groovy runapp.groovy <script> <dir to watch> [<static file dir>]"
}
