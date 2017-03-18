/*
 * MatchThreeGameTest.java
 * Copyright (c) 2017 Markus Himmel and others
 * This file is distributed under the terms of the MIT license.
 */

package edu.kit.informatik.matchthree.tests;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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
//import edu.kit.informatik.matchthree.framework.interfaces.Move;

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
        assertEquals(15, game.getScore());

        game.acceptMove(new MoveFactoryImplementation()
            .rotateSquareClockwise(Position.at(0, 1)).reverse());

        assertEquals("aabc;acac;acba", board.toTokenString());
        assertEquals(47, game.getScore());

    }
}

// vim: set expandtab:
