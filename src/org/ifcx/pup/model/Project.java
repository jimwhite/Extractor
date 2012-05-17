package org.ifcx.pup.model;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.domain.relations.CreatedBy;

import java.util.Collection;

public interface Project extends VertexFrame
{
    @Property("name")
    public void setName(String name);
    @Property("name")
    public String getName();
    @Property("lang")
    public void setLanguage(String language);
    @Property("lang")
    public int getLanguage();

    @Relation(label="created", direction=Direction.INVERSE)
    public Collection<Person> getCreatedByPerson();

    @Adjacency(label = "created", direction = Direction.INVERSE)
    public Collection<CreatedBy> getCreatedBy();
}
