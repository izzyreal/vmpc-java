// Copyright (C) 2009, 2010, 2014 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package com.mpc.tootextensions;

import java.util.List;
import java.util.Observable;

/**
 * A Source is a composite event iterator. This is the contract required
 * by a Sequencer to be able to use arbitrary track based representations
 * of music. Such representations may be edited whilst being played. In general
 * the representation need not be known a priori, it could even be generated in
 * real-time. An implementation of this class can be properly decoupled from the
 * underlying representation. The implementation of this class should know about
 * its underlying representation, that representation should not know about this
 * implementation. Clients of this class need not know about any such
 * representation or any specific implementation of this class.
 * 
 * Note that this class is a composition of iterators, it can only be used by one client
 * at a time.
 * 
 * @author st
 * 
 */
public abstract class Source extends Observable
{
    private SynchronousControl control;
    
    /*
     * set this in subclass
     */
    protected String name;
    
        /**
         * @return the name of this Source 
         */
        public String getName() {
            return name;
        }
        
        /**
         * @return the resolution in ticks per quarter note
         */
        public abstract int getResolution();
        
    /**
     * @return the List of Tracks
     */
    abstract List<Track> getTracks();

    /**
     * Called by the Sequencer once when it begins using this source to
     * privately provide us with its synchronous control interface
     * @param ctl
     */
  
    public void control(SynchronousControl ctl) {
        control = ctl;
    }
            
    /**
     * Should only be called by the Sequencer.
     * Play events as they become due.
     * @param targetTick the tick to play until.
     */
    public void playToTick(long targetTick) {
        for ( Track trk : getTracks() ) {
            while ( trk.getNextTick() <= targetTick ) {
                trk.playNext();
            }
        }
    }

    /**
     * Should only be called by the Sequencer.
     * Turn off track outputs on a stop condition
     */
        protected void stopped() {
            for ( Track trk : getTracks() ) {
                trk.off(true);
            }           
        }
        
        /**
         * Should be called as a result of a call to playToTick()
         * Typically when the Track representing the tempo map is 'played' and tempo
         * events are reached.
         * @param bpm
         */
        protected void setBpm(float bpm) {
            if ( control != null ) control.setBpm(bpm);
        }
               
        /**
         * The interface used to call back to the Sequencer synchronously with
         * its real time thread.
         */
        public interface SynchronousControl
        {
            public void setBpm(float bpm);
        }
}