package com.github.timm.cucumber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class ModuloCounterTest {

    private ModuloCounter counter;

    @Test
    public void shouldCountMod1() {
        this.counter = new ModuloCounter("NoModule");

        assertThat(this.counter.next(), is(0));
        assertThat(this.counter.next(), is(0));
    }

    @Test
    public void shouldCountMod7() {
        this.counter = new ModuloCounter("{c:7}");

        assertThat(this.counter.next(), is(0));
        assertThat(this.counter.next(), is(1));
        assertThat(this.counter.next(), is(2));
        assertThat(this.counter.next(), is(3));
        assertThat(this.counter.next(), is(4));
        assertThat(this.counter.next(), is(5));
        assertThat(this.counter.next(), is(6));
        assertThat(this.counter.next(), is(0));
        assertThat(this.counter.next(), is(1));
        assertThat(this.counter.next(), is(2));
        assertThat(this.counter.next(), is(3));
        assertThat(this.counter.next(), is(4));
    }

    @Test
    public void shouldCountMod6() {
        this.counter = new ModuloCounter("Group{c:6}-Parallel{c}IT");

        assertThat(this.counter.next(), is(0));
        assertThat(this.counter.next(), is(1));
        assertThat(this.counter.next(), is(2));
        assertThat(this.counter.next(), is(3));
        assertThat(this.counter.next(), is(4));
        assertThat(this.counter.next(), is(5));
        assertThat(this.counter.next(), is(0));
        assertThat(this.counter.next(), is(1));
        assertThat(this.counter.next(), is(2));
        assertThat(this.counter.next(), is(3));
        assertThat(this.counter.next(), is(4));
        assertThat(this.counter.next(), is(5));
    }

    @Test
    public void shouldCountMod4() {
        this.counter = new ModuloCounter("fhqwgads-{f}Parallel{c}ITGroup{c:4}");

        assertThat(this.counter.next(), is(0));
        assertThat(this.counter.next(), is(1));
        assertThat(this.counter.next(), is(2));
        assertThat(this.counter.next(), is(3));
        assertThat(this.counter.next(), is(0));
        assertThat(this.counter.next(), is(1));
        assertThat(this.counter.next(), is(2));
        assertThat(this.counter.next(), is(3));
        assertThat(this.counter.next(), is(0));
        assertThat(this.counter.next(), is(1));
        assertThat(this.counter.next(), is(2));
        assertThat(this.counter.next(), is(3));
    }

}