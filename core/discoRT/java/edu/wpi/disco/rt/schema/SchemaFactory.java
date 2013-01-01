package edu.wpi.disco.rt.schema;

import org.picocontainer.PicoContainer;

public interface SchemaFactory {

   long getUpdateDelay ();

   Class<? extends Schema> getSchemaType ();

   Schema create (PicoContainer container);
}