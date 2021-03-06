package edu.wpi.always.user.owl;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public interface Ontology {

   String DOCUMENT_IRI_STRING = "http://www.wpi.org/ontologies/ontology/AlwaysOntology.owl";
   IRI DOCUMENT_IRI = IRI.create(DOCUMENT_IRI_STRING);

   OWLOntologyManager getManager ();

   OWLOntology getOntology ();

   OWLDataFactory getFactory ();

   PrefixManager getPm ();

   OWLReasoner getReasoner ();
   
   void reset ();
   
   void ensureConsistency ();
}
