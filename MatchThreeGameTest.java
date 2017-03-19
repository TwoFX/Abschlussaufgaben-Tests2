/*
 * MatchThreeGameTest.java
 * Copyright (c) 2017 Markus Himmel and others
 * This file is distributed under the terms of the MIT license.
 */

package edu.kit.informatik.matchthree.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.kit.informatik.matchthree.MatchThreeBoard;
import edu.kit.informatik.matchthree.MatchThreeGame;
import edu.kit.informatik.matchthree.MaximumDeltaMatcher;
import edu.kit.informatik.matchthree.MoveFactoryImplementation;
import edu.kit.informatik.matchthree.framework.Delta;
import edu.kit.informatik.matchthree.framework.DeterministicStrategy;
import edu.kit.informatik.matchthree.framework.Position;
import edu.kit.informatik.matchthree.framework.Token;
import edu.kit.informatik.matchthree.framework.interfaces.Board;
import edu.kit.informatik.matchthree.framework.interfaces.Matcher;

public class MatchThreeGameTest
{
    @Rule
    public ExpectedException excpetion = ExpectedException.none();

    @Test
    public void basicTest()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("abc"),
            "b b ;abba;aaab");

        board.setFillingStrategy(new DeterministicStrategy(
            Token.iterator("aaaaa"), Token.iterator("bbbcabca"),
            Token.iterator("abbabab"), Token.iterator("aabcc")));

        Set<Delta> deltas = new HashSet<Delta>();
        deltas.add(Delta.dxy(1, 0));
        MaximumDeltaMatcher matcher = new MaximumDeltaMatcher(deltas);


        MatchThreeGame game = new MatchThreeGame(board, matcher);
        game.initializeBoardAndStart();

        assertEquals("acba;abba;abaa", board.toTokenString());
        assertEquals(18, game.getScore());

        game.acceptMove(new MoveFactoryImplementation()
            .rotateSquareClockwise(Position.at(0, 1)).reverse());

        assertEquals("aabc;acac;acba", board.toTokenString());
        assertEquals(66, game.getScore());
    }

    private static class IntrospectiveMatcher implements Matcher
    {
        private Matcher inner;
        private Iterator<Set<Position>> toCheck;

        public IntrospectiveMatcher(Matcher inner, Iterator<Set<Position>> toCheck)
        {
            this.inner = inner;
            this.toCheck = toCheck;
        }

        public Set<Set<Position>> match(Board board, Position initial)
        {
            assertTrue(toCheck.hasNext());
            Set<Position> current = toCheck.next();
            Position[] all = current.toArray(new Position[0]);
            assertEquals(1, all.length);
            assertEquals(all[0], initial);
            return inner.match(board, initial);
        }

        public Set<Set<Position>> matchAll(Board board, Set<Position> initial)
        {
            assertTrue(toCheck.hasNext());
            Set<Position> current = toCheck.next();
            assertEquals(current, initial);
            return inner.matchAll(board, initial);
        }
    }

    @Test
    public void inspectionTest()
    {
        // So the idea of this test is to check that the game only tries to
        // match exactly the positions that it needs to. In theory, it is
        // possible to write a conforming implementation which fails this test
        // because it calls match/matchAll multiple times for one step of a
        // chain reaction.
    }

    private static class PredeterminedMatcher implements Matcher
    {
        private Iterator<Set<Set<Position>>> toReturn;

        public PredeterminedMatcher(Iterator<Set<Set<Position>>> toReturn)
        {
            this.toReturn = toReturn;
        }

        public Set<Set<Position>> match(Board board, Position initial)
        {
            if (toReturn.hasNext())
                return toReturn.next();

            return Collections.emptySet();
        }

        public Set<Set<Position>> matchAll(Board board, Set<Position> initial)
        {
            if (toReturn.hasNext())
                return toReturn.next();

            return Collections.emptySet();
        }
    }

    @Test
    public void subsetMatchTest()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("abcde"),
            "ccc;bbb;aaa");

        board.setFillingStrategy(new DeterministicStrategy(
            Token.iterator("de"), Token.iterator("d"), Token.iterator("de")));

        Set<Set<Position>> matches = new HashSet<Set<Position>>();

        Set<Position> match1 = new HashSet<Position>();
        match1.add(Position.at(1, 0));
        match1.add(Position.at(2, 0));
        matches.add(match1);

        Set<Position> match2 = new HashSet<Position>();
        match2.add(Position.at(0, 1));
        match2.add(Position.at(0, 2));
        match2.add(Position.at(1, 2));
        match2.add(Position.at(2, 2));
        matches.add(match2);

        Set<Position> match3 = new HashSet<Position>();
        match3.add(Position.at(0, 2));
        match3.add(Position.at(1, 2));
        match3.add(Position.at(2, 2));
        matches.add(match3);

        Set<Position> match4 = new HashSet<Position>();
        match4.add(Position.at(2, 1));
        match4.add(Position.at(0, 2));
        match4.add(Position.at(1, 2));
        match4.add(Position.at(2, 2));
        matches.add(match4);

        PredeterminedMatcher matcher = new PredeterminedMatcher(
            Arrays.asList(matches).iterator());

        MatchThreeGame game = new MatchThreeGame(board, matcher);
        game.initializeBoardAndStart();

        assertEquals("ede;dcd;cbc", board.toTokenString());
        assertEquals(20, game.getScore());
    }

    private static class MultiMatcher implements Matcher
    {
        private Matcher[] matchers;
        public MultiMatcher(Matcher... matchers)
        {
            this.matchers = matchers;
        }

        public Set<Set<Position>> match(Board board, Position initial)
        {
            return Arrays.stream(matchers).flatMap(a -> a.match(board, initial).stream())
                .collect(Collectors.toSet());
        }

        public Set<Set<Position>> matchAll(Board board, Set<Position> initial)
        {
            return Arrays.stream(matchers).flatMap(a -> a.matchAll(board, initial).stream())
                .collect(Collectors.toSet());
        }
    }

    @Test
    public void example22Test()
    {
        MatchThreeBoard board = new MatchThreeBoard(Token.set("AXO*"),
            "O*O;***;O*O;O*O");

        board.setFillingStrategy(new DeterministicStrategy(
            Token.iterator("AOA**"), Token.iterator("AXAXA"),
            Token.iterator("A**A*")));

        Set<Delta> a = new HashSet<Delta>();
        a.add(Delta.dxy(1, 0));

        Set<Delta> b = new HashSet<Delta>();
        b.add(Delta.dxy(0, 1));

        Matcher m = new MultiMatcher(new MaximumDeltaMatcher(a),
            new MaximumDeltaMatcher(b));
        MatchThreeGame g = new MatchThreeGame(board, m);
        g.initializeBoardAndStart();

        assertEquals("*A*;*XA;AA*;OX*", board.toTokenString());
        assertEquals(49, g.getScore());
    }
}

// vim: set expandtab:
