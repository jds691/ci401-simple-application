package com.neo.game;

import javafx.util.Pair;

// This is the dumbest solution I've ever some up with
// We match the colors to the block formations and use a fixed transformation table
// This is not easily extendable in the slightest, but I'm tired
// Kekw I'm not even drunk writing this one

// My brain is melting working out all of this maths
// To accurately see how I feel: https://www.youtube.com/watch?v=Sv8LHpezbLw

/*
 * Take a 4 x 2 block
 * |
 * |_ _ _ _
 *
 * The transformations involved for rotating right are:
 *
 * (0,0) = (1, 0) = (+1, 0)
 * (0,1) = (0, 0) = (0, -1)
 * (1,1) = (0, 1) = (-1, 0)
 * (2,1) = (0, 2) = (-2, +1)
 * (3,1) = (0, 3) = (-3, +2)
 *
 * Which looks like:
 *
 *  _ _
 * |
 * |
 * |
 * |
 */
//The board gets searched top-down, left-right. Therefore we can make this so the array of coordinates aligns to that order
public class TransformationManager {
    public static Pair<Integer, Integer>[] requestCoordinateTransformations(BlockFormation block, boolean rotateRight, RotationState targetState) {
        // Behold, the 3D transformation array
        Pair<Integer, Integer>[][] transformationTable = new Pair[0][];

        switch (block.color) {
            case Block.Color.Cyan: // I_BLOCK
                transformationTable = new Pair[][]{
                        { // 270 -> 0
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0)
                        },
                        { // 0 -> 90
                        },
                        { // 90 -> 180
                        },
                        { // 180 -> 270
                        }
                };
                break;

            case Block.Color.Blue: // J_BLOCK
                transformationTable = new Pair[][]{
                        { // 270 -> 0
                        },
                        { // 0 -> 90
                        },
                        { // 90 -> 180
                        },
                        { // 180 -> 270
                        }
                };
                break;

            case Block.Color.Orange: // L_BLOCK
                transformationTable = new Pair[][]{
                        /*
                           |
                           |
                           |
                           |
                        _ _
                         */
                        { // 270 -> 0
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0)
                        },
                        /*
                         |
                         |_ _ _ _
                         */
                        { // 0 -> 90
                                new Pair<>(+1, 0),
                                new Pair<>(0, -1),
                                new Pair<>(-1, 0),
                                new Pair<>(-2, +1),
                                new Pair<>(-3, +2)
                        },
                        /*
                         _ _
                        |
                        |
                        |
                        |
                         */
                        { // 90 -> 180
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0)
                        },
                        /*
                        _ _ _ _
                               |
                               |
                         */
                        { // 180 -> 270
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0),
                                new Pair<>(0, 0)
                        }
                };
                break;

            case Block.Color.Yellow: // S_BLOCK
                transformationTable = new Pair[][]{
                        { // 270 -> 0
                        },
                        { // 0 -> 90
                        },
                        { // 90 -> 180
                        },
                        { // 180 -> 270
                        }
                };
                break;

            case Block.Color.Purple: // T_BLOCK
                transformationTable = new Pair[][]{
                        { // 270 -> 0
                        },
                        { // 0 -> 90
                        },
                        { // 90 -> 180
                        },
                        { // 180 -> 270
                        }
                };
                break;

            case Block.Color.Red: // Z_BLOCK
                transformationTable = new Pair[][]{
                        { // 270 -> 0
                        },
                        { // 0 -> 90
                        },
                        { // 90 -> 180
                        },
                        { // 180 -> 270
                        }
                };
                break;
        }

        Pair<Integer, Integer>[] targetTransformation = transformationTable[targetState.ordinal()];

        if (!rotateRight) {
            // The transformations need swizzled to be inverted
            for (int i = 0; i < targetTransformation.length; i++) {
                Pair<Integer, Integer> currentTransform = targetTransformation[i];

                targetTransformation[i] = new Pair<>(currentTransform.getKey() * -1, currentTransform.getValue() * -1);
            }
        }

        return targetTransformation;
    }
}
