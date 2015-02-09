package com.urbanairship.ram.demo;

import com.google.common.collect.ImmutableSet;

public interface ProjectApi {

        void put(String thing);

        ImmutableSet<String> get();

}
