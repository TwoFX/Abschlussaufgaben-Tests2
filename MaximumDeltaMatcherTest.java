/*
 * MaximumDeltaMatcherTest.java
 * Copyright (c) 2017 Markus Himmel and others
 * This file is distributed under the terms of the MIT license.
 */

package edu.kit.informatik.matchthree.tests;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.kit.informatik.matchthree.MatchThreeBoard;
import edu.kit.informatik.matchthree.MaximumDeltaMatcher;
import edu.kit.informatik.matchthree.framework.Delta;
import edu.kit.informatik.matchthree.framework.Position;
import edu.kit.informatik.matchthree.framework.Token;

public class MaximumDeltaMatcherTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void Example1Test()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("An"),
            "An;An;AA");

        HashSet<Delta> deltas = new HashSet<>();
        deltas.add(Delta.dxy(0, 1));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);
        HashSet<Position> initial = new HashSet<>();
        initial.add(Position.at(0, 2));

        Set<Set<Position>> expected = new HashSet<Set<Position>>();

        Set<Position> match1 = new HashSet<Position>();
        match1.add(Position.at(0, 0));
        match1.add(Position.at(0, 1));
        match1.add(Position.at(0, 2));
        expected.add(match1);

        assertEquals(expected, matcher.matchAll(board, initial));
        assertEquals(expected, matcher.match(board, Position.at(0, 2)));

        initial.add(Position.at(0, 1));
        assertEquals(expected, matcher.matchAll(board, initial));

        initial.add(Position.at(1, 0));
        Set<Position> match2 = new HashSet<Position>();
        match2.add(Position.at(1, 0));
        match2.add(Position.at(1, 1));
        expected.add(match2);

        assertEquals(expected, matcher.matchAll(board, initial));
    }

    @Test
    public void Example2Test()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("On"),
            "nOO;OOO;nnO");

        HashSet<Delta> deltas = new HashSet<>();
        deltas.add(Delta.dxy(0, -1));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);

        Set<Set<Position>> expected = new HashSet<Set<Position>>();

        Set<Position> match1 = new HashSet<Position>();
        match1.add(Position.at(2, 0));
        match1.add(Position.at(2, 1));
        match1.add(Position.at(2, 2));
        expected.add(match1);

        assertEquals(expected, matcher.match(board, Position.at(2, 1)));

        deltas = new HashSet<>();
        deltas.add(Delta.dxy(-1, -1));
        deltas.add(Delta.dxy(2, 1));
        deltas.add(Delta.dxy(1, 2));
        deltas.add(Delta.dxy(-2, 1));
        matcher = new MaximumDeltaMatcher(deltas);

        expected = new HashSet<Set<Position>>();
        match1 = new HashSet<Position>();
        match1.add(Position.at(2, 0));
        match1.add(Position.at(2, 1));
        match1.add(Position.at(2, 2));
        match1.add(Position.at(1, 0));
        match1.add(Position.at(1, 1));
        match1.add(Position.at(0, 1));
        expected.add(match1);

        assertEquals(expected, matcher.match(board, Position.at(2, 1)));
        assertEquals(expected, matcher.match(board, Position.at(1, 1)));

        Set<Position> match2 = new HashSet<Position>();
        match2.add(Position.at(0, 0));
        match2.add(Position.at(1, 2));
        expected.add(match2);

        Set<Position> match3 = new HashSet<Position>();
        match3.add(Position.at(0, 2));
        expected.add(match3);

        HashSet<Position> initial = new HashSet<Position>();
        initial.add(Position.at(0, 0));
        initial.add(Position.at(1, 0));
        initial.add(Position.at(2, 0));
        initial.add(Position.at(0, 2));
        initial.add(Position.at(1, 2));
        initial.add(Position.at(2, 2));

        assertEquals(expected, matcher.matchAll(board, initial));
    }

    @Test
    public void doNotAllowTampering1Test()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("ab"),
            "aaa;bbb");

        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(2, 0));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);
        deltas.add(Delta.dxy(1, 0));

        Set<Set<Position>> expected = new HashSet<Set<Position>>();

        Set<Position> match1 = new HashSet<Position>();
        match1.add(Position.at(0, 0));
        match1.add(Position.at(2, 0));
        expected.add(match1);

        assertEquals(expected, matcher.match(board, Position.at(0, 0)));
    }

    @Test
    public void doNotAllowTampering2Test()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("ab"),
            "aaa;bbb");

        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(2, 0));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);
        deltas.add(null);

        Set<Set<Position>> expected = new HashSet<Set<Position>>();

        Set<Position> match1 = new HashSet<Position>();
        match1.add(Position.at(0, 0));
        match1.add(Position.at(2, 0));
        expected.add(match1);

        assertEquals(expected, matcher.match(board, Position.at(0, 0)));
    }

    @Test
    public void doNotAllowTampering3Test()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("ab"),
            "aaa;bbb");

        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(2, 0));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);
        deltas.add(Delta.dxy(0, 0));

        Set<Set<Position>> expected = new HashSet<Set<Position>>();

        Set<Position> match1 = new HashSet<Position>();
        match1.add(Position.at(0, 0));
        match1.add(Position.at(2, 0));
        expected.add(match1);

        assertEquals(expected, matcher.match(board, Position.at(0, 0)));
    }

    @Test
    public void traverseNullTest()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("ab"),
            "a a;   ;aaa");

        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(1, 0));
        deltas.add(Delta.dxy(0, 1));
        deltas.add(Delta.dxy(1, 1));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);

        Set<Set<Position>> expected = new HashSet<Set<Position>>();

        Set<Position> match1 = new HashSet<Position>();
        match1.add(Position.at(0, 0));
        expected.add(match1);

        assertEquals(expected, matcher.match(board, Position.at(0, 0)));
    }

    @Test
    public void doNotCombineStepsTest()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("ab"),
            "ab;ba");

        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(1, 0));
        deltas.add(Delta.dxy(0, 1));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);

        Set<Set<Position>> expected = new HashSet<Set<Position>>();

        Set<Position> match1 = new HashSet<Position>();
        match1.add(Position.at(0, 0));
        expected.add(match1);

        assertEquals(expected, matcher.match(board, Position.at(0, 0)));
    }

    @Test
    public void invalidDelta1Test()
    {
        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(1, 1));
        deltas.add(Delta.dxy(0, 0));
        deltas.add(Delta.dxy(-1, 1));

        exception.expect(IllegalArgumentException.class);
        new MaximumDeltaMatcher(deltas);
    }

    @Test
    public void invalidDelta2Test()
    {
        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(1, 1));
        deltas.add(null);
        deltas.add(Delta.dxy(-1, 1));

        exception.expect(RuntimeException.class);
        new MaximumDeltaMatcher(deltas);
    }

    @Test
    public void basicTest()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("ab"),
            "aaaaaaaaaa;          ");

        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(5, 0));
        deltas.add(Delta.dxy(3, 0));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);

        Set<Set<Position>> expected = new HashSet<Set<Position>>();
        Set<Position> match1 = new HashSet<Position>();

        match1.add(Position.at(0, 0));
        match1.add(Position.at(1, 0));
        match1.add(Position.at(2, 0));
        match1.add(Position.at(3, 0));
        match1.add(Position.at(4, 0));
        match1.add(Position.at(5, 0));
        match1.add(Position.at(6, 0));
        match1.add(Position.at(7, 0));
        match1.add(Position.at(8, 0));
        match1.add(Position.at(9, 0));
        expected.add(match1);

        assertEquals(expected, matcher.match(board, Position.at(0, 0)));
    }

    @Test
    public void nullDeltasTest()
    {
        exception.expect(RuntimeException.class);
        new MaximumDeltaMatcher(null);
    }

    @Test
    public void emptyDeltasTest()
    {
        exception.expect(IllegalArgumentException.class);
        new MaximumDeltaMatcher(new HashSet<Delta>());
    }

    @Test(timeout = 5000)
    public void performance1Test()
    {
        int N = 1000000;
        MatchThreeBoard board = new MatchThreeBoard(Token.set("ab"), N, 2);

        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(1, 0));

        Set<Set<Position>> expected = new HashSet<Set<Position>>();
        Set<Position> match1 = new HashSet<Position>();

        for (int i = 0; i < N; i++)
        {
            board.setTokenAt(Position.at(i, 0), new Token("a"));
            match1.add(Position.at(i, 0));
        }
        expected.add(match1);

        Set<Set<Position>> got = new MaximumDeltaMatcher(deltas).match(board,
            Position.at(0, 0));

        assertEquals(expected, got);
    }

    @Test
    public void nullStartTest()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("ab"), 4, 4);
        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(0, 1));
        deltas.add(Delta.dxy(1, 0));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);

        Set<Set<Position>> it = matcher.match(board, Position.at(0, 0));
        assertTrue(it.size() <= 1);
        if (it.size() == 1)
        {
            assertTrue(it.contains(Collections.emptySet()));
        }
    }

}

// vim: set expandtab:
