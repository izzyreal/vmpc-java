package com.mpc.tootextensions;
   /**
     * An iterator of arbitrary tick-based events which is able to play
     * events at the correct time and control the event output in other ways.
     */
    public interface Track
    {
        public final static long MAX_TICK = Long.MAX_VALUE;
        
            /**
             * Return the next event tick without changing iterator position.
             * @return the next tick or MAX_TICK if none
             */
            public long getNextTick();
            
            /**
             * Play the next event and increment iterator position.
             */
            public void playNext();
            
            /**
             * Turn this track off, turn notes off etc.
             * With Midi, reset controllers if stop (otherwise not in mute)
             */
            public void off(boolean stop);
            
            /**
             * Return our name, which should be unique
             * for each Track belonging to this Source.
             * @return our name
             */
            public String getName();
                        
    }