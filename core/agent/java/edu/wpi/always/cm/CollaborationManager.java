package edu.wpi.always.cm;

import edu.wpi.always.*;
import edu.wpi.always.cm.engagement.GeneralEngagementPerceptorImpl;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.registries.SchemaRegistry;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.perceptor.Perceptor;
import edu.wpi.disco.rt.realizer.*;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import java.util.*;

public class CollaborationManager implements ICollaborationManager {

   private static final long ARBITRATOR_INTERVAL = 50;
   private static final long PERCEPTORS_INTERVAL = 200;
   private final Scheduler scheduler = new Scheduler();
   private final MutablePicoContainer pico;
   private final List<SchemaRegistry> schemaRegistries = new ArrayList<SchemaRegistry>();
   private final List<SimpleRegistry> picoRegistries = new ArrayList<SimpleRegistry>();

   public CollaborationManager (PicoContainer programContainer) {
      pico = new DefaultPicoContainer(new OptInCaching(), programContainer);
      configureContainer();
   }

   private void configureContainer () {
      pico.addComponent(pico);
      pico.as(Characteristics.CACHE).addComponent(
            PrimitiveBehaviorControlImpl.class);
      pico.as(Characteristics.CACHE).addComponent(RealizerImpl.class);
      pico.addComponent(PluginSpecificActionRealizer.class);
      pico.addComponent(FocusRequestRealizer.class);
      pico.addComponent(FuzzyArbitrationStrategy.class);
      pico.as(Characteristics.CACHE).addComponent(
            CandidateBehaviorsContainerImpl.class);
      pico.as(Characteristics.CACHE).addComponent(Arbitrator.class);
      pico.addComponent(IdleBehaviorsImpl.class);
      pico.as(Characteristics.CACHE).addComponent(ResourceMonitorImpl.class);
      pico.addComponent(scheduler);
      pico.as(Characteristics.CACHE).addComponent(SchemaManager.class);
   }

   @Override
   public void addRegistry (Registry registry) {
      if ( registry instanceof SimpleRegistry )
         picoRegistries.add((SimpleRegistry) registry);
      if ( registry instanceof SchemaRegistry )
         schemaRegistries.add((SchemaRegistry) registry);
   }

   @Override
   public void start () {
      initDisco();
      for (SimpleRegistry registry : picoRegistries) {
         registry.register(pico);
      }
      SchemaManager schemaManager = getContainer().getComponent(
            SchemaManager.class);
      for (SchemaRegistry registry : schemaRegistries) {
         registry.register(schemaManager);
      }
      pico.as(Characteristics.CACHE).addComponent(DummyMotionPerceptor.class);
      pico.as(Characteristics.CACHE).addComponent(
            GeneralEngagementPerceptorImpl.class);
      pico.as(Characteristics.CACHE).addComponent(
            DummyEmotiveFacePerceptor.class);
      configurePrimitiveRealizerFactory();
      Arbitrator arbitrator = getContainer().getComponent(Arbitrator.class);
      @SuppressWarnings("rawtypes")
      java.util.List<Perceptor> perceptors = getContainer().getComponents(
            Perceptor.class);
      schedule(arbitrator, ARBITRATOR_INTERVAL);
      for (Perceptor<?> p : perceptors) {
         schedule(p, PERCEPTORS_INTERVAL);
      }
      schemaManager.startUp();
   }

   private void configurePrimitiveRealizerFactory () {
      PrimitiveRealizerFactoryImpl realizerFactory = new PrimitiveRealizerFactoryImpl(
            pico);
      pico.addComponent(realizerFactory);
      realizerFactory.registerAllRealizerInContainer();
   }

   private void schedule (Runnable runnable, long interval) {
      scheduler.schedule(runnable, interval);
   }

   private void initDisco () {
      Disco disco = new DiscoBootstrapper()
            .bootstrap(new Agent("agent"), false);
      getContainer().addComponent(new DiscoSynchronizedWrapper(disco));
   }

   public MutablePicoContainer getContainer () {
      return pico;
   }
}
