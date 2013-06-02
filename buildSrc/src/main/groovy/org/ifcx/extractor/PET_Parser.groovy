package org.ifcx.extractor

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

class PET_Parser
{
    File pet_file

    FileInputStream fis
    FileChannel fc
    ByteBuffer bbuf
    CharBuffer cbuf

    /** Map from sentence # (1-based) to file offset (0-based). */
    Map<Integer, Parse> parses

    def load(File pet_file)
    {
        this.pet_file = pet_file

        fis = new FileInputStream(pet_file);
        fc = fis.getChannel();

        println pet_file.size()
        println fc.size()

        // Create a read-only CharBuffer on the file
        bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
        cbuf = Charset.forName('UTF-8').newDecoder().decode(bbuf);

        parses = [:]

        def m = (~/^\[(\d+\)]\s*\((\d+) of (\d+)\)\s*{\d+}\s*`(.*)'$/).matcher(cbuf)

        m.each {
            def (sent_num, n, tot, sent) = it
            println "$sent_num ($n of $tot) '$sent'"
        }
    }


    class Parse {
        Integer num
        String sentence
        Integer charOffset

    }

    static void main(String[] args)
    {
//        Charset.availableCharsets().each { k, v -> println "$k : $v"}
        def p = new PET_Parser()
        p.load(new File('data/comments.pet'))
    }
}
