package de.fra_uas.fb2.mobiledevices.abeautifulmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

class GameActivity : AppCompatActivity() {

    private var gameMode: String? = null
    private var tvStrategy: TextView? = null

    var tvMatrix00: TextView? = null
    var tvMatrix01: TextView? = null
    var tvMatrix10: TextView? = null
    var tvMatrix11: TextView? = null

    var textMatrixFields = mutableListOf<TextView?>()

    private var buttonActionA: Button? = null
    private var buttonActionB: Button? = null
    private var buttonDismiss: Button? = null

    private var tvYouChose: TextView? = null
    private var tvOppChose: TextView? = null
    private var tvYouObtain: TextView? = null
    private var tvOppObtain: TextView? = null

    private val game = Game()
    private val gameData: GameData = GameData

    private val maxMatrixValue = 5
    private val minMatrixValue = -5

    private var opponentNashLetter = ' '
    private var playerNashLetter = ' '


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        tvStrategy = findViewById(R.id.tvStrategy)

        tvMatrix00 = findViewById(R.id.tvOption00)
        tvMatrix01 = findViewById(R.id.tvOption01)
        tvMatrix10 = findViewById(R.id.tvOption10)
        tvMatrix11 = findViewById(R.id.tvOption11)
        textMatrixFields.add(tvMatrix00)
        textMatrixFields.add(tvMatrix01)
        textMatrixFields.add(tvMatrix10)
        textMatrixFields.add(tvMatrix11)

        buttonActionA = findViewById(R.id.buttonActionA)
        buttonActionB = findViewById(R.id.buttonActionB)
        buttonDismiss = findViewById(R.id.buttonDismiss)

        tvYouChose = findViewById(R.id.tvYouChoose)
        tvOppChose = findViewById(R.id.tvOppChoose)
        tvYouObtain = findViewById(R.id.tvYouObtain)
        tvOppObtain = findViewById(R.id.tvOppObtain)

        // hint which strategy is played

        tvStrategy?.text = gameData.gameModeNew

        gameData.gameModeOld = gameData.gameModeNew


        // calculate random numbers
        game.generateBoard()

        game.determineGameMode()

        // display them
        game.displayBoard()


        buttonActionA!!.setOnClickListener {
            deactivateButtons()
            game.playerAction = 'A'

            if (opponentNashLetter != ' ')
                CustomToast.makeText(this, "There is a single Nash equilibrium in this\ngame:" +
                        "My Action: ${playerNashLetter}\n" +
                        "Opponent Action: $opponentNashLetter")

            displayResult()

            buttonActionA?.setEnabled(false)
            buttonActionB?.setEnabled(false)
        }

        buttonActionB!!.setOnClickListener {
            deactivateButtons()
            game.playerAction = 'B'

            // check if single nash case
            if (opponentNashLetter != ' ')
                CustomToast.makeText(this, "There is a single Nash equilibrium in this" +
                        "game:\n" +
                        "My Action: ${playerNashLetter}\n" +
                        "Opponent Action: $opponentNashLetter")

            displayResult()
            buttonActionA?.setEnabled(false)
            buttonActionB?.setEnabled(false)
        }

        buttonDismiss!!.setOnClickListener {
            var finishedStatus = 0

            val intent = Intent(this, MainActivity::class.java)

            // for toast

            if (game.playerAction == ' ')
                finishedStatus = 0
            else
                finishedStatus = 1

            intent.putExtra("finished", finishedStatus)
            startActivity(intent)
        }

    }

    private fun displayResult() {

        // multiple nash equilibrium
        if (game.opponentAction == ' ') {
            CustomToast.makeText(
                this,
                "There are multiple Nash equilibria\n in this game.\n" +
                        "Opponent cannot choose an action.\n" +
                        "Game cannot be finished.")
            return
        }

        val result: Pair<Int, Int> = game.calculateResult()

        // update scores of both players
        gameData.playerScore += result.first
        gameData.opponentScore += result.second

        // update total games played
        gameData.gamesPlayed++

//        debugAll(result.first, result.second)

        val youChoseText = "You chose: Action ${game.playerAction}"
        val oppChoseText = "Opponent chose: Action ${game.opponentAction}"
        val youObtainText = "You obtain: ${result.first} points"
        val oppObtainText = "Opponent obtains: ${result.second} points"

        tvYouChose?.text = youChoseText
        tvOppChose?.text = oppChoseText
        tvYouObtain?.text = youObtainText
        tvOppObtain?.text = oppObtainText
    }

    private fun deactivateButtons() {
        buttonActionA?.setBackgroundResource(R.color.deactivatedButtonBackground)
        buttonActionB?.setBackgroundResource(R.color.deactivatedButtonBackground)

        buttonActionA?.setTextColor(
            ContextCompat.getColor(
                this@GameActivity,
                R.color.deactivatedButtonForeground
            )
        )
        buttonActionB?.setTextColor(
            ContextCompat.getColor(
                this@GameActivity,
                R.color.deactivatedButtonForeground
            )
        )
    }

    inner class Game {

        // the game board is indexed as
        //  ------------------------------------------
        // |         0           |          1        |
        // -------------------------------------------
        // |        2            |          3        |
        // -------------------------------------------
        var board = Array(4) { Pair(0, 0) }

        var playerAction: Char = ' '
        var opponentAction: Char = ' '

        private fun randomGame() {
            val randomNumber = (0..1).random()
            // 50 / 50
            if (randomNumber == 0)
                opponentAction = 'A'
            else if (randomNumber == 1)
                opponentAction = 'B'
        }

        private fun greedyGame() {

            var maxUtility = Int.MIN_VALUE

            // find max value for opponent in matrix
            for (i in board.indices)
                if (board[i].second >= maxUtility)
                    maxUtility = board[i].second

            Log.d("XXXLOG", "maxUtility $maxUtility")


            // get column name of that matrix, eg: A, B
            // even -> A
            // odd ->  B
            // 0 -> A
            // 1 -> B
            // 2 -> A
            // 3 -> B
            var boardIndex = board.indexOfFirst { it.second == maxUtility }

            if (boardIndex % 2 == 0)
                opponentAction = 'A'
            else
                opponentAction = 'B'
        }

        private fun cautiousGame() {

            // maximizing minimum utility
            var columnOneSum = 0
            var columnTwoSum = 0

            // only left colums
            // matrix is saved into a single dimensional array of Pairs
            // so 2 is the offset to jump to the same colum again
            for (i in 0..3 step 2)
                columnOneSum += board[i].second

            for (i in 0+1..3+1 step 2)
                columnTwoSum += board[i].second

            if (columnOneSum > columnTwoSum)
                opponentAction = 'A'
            else
                opponentAction = 'B'

        }

        // returns true if single nash case
        private fun nashGame() {

            // tests for lectures Slides Test runs
            // TestSingleNash()
            // TestMultipleNash()

            val playerStrategy = calculatePlayerNash()
            val opponentStrategy = calculateOpponentNash()

            var boardIntersection = playerStrategy.intersect(opponentStrategy.toSet())

            // we have exactly 1 nash equilibrium
            if (boardIntersection.size == 1) {
                var index = boardIntersection.first()
                if (index % 2 == 0) {
                    opponentAction = 'A'
                    playerNashLetter = 'B'
                }
                else {
                    opponentAction = 'B'
                    playerNashLetter = 'A'
                }

                opponentNashLetter = opponentAction

            }
            else if (boardIntersection.size > 1)
                opponentAction = ' '

        }

        // my idea behind the implementation calcluateNash functions are
        // that we create two list for the player and the opponent
        // for their best choices after the other player has done their step
        // for example
        // the game board is indexed as
        //  ------------------------------------------
        // |         0           |          1        |
        // -------------------------------------------
        // |        2            |          3        |
        // -------------------------------------------
        //
        // now if both players have their best choices on the same index
        // it means there is a nash equilibrium
        // i did come up with this myself and tested it and it seems working so far
        // we will then have two list for example
        // Player   (1, 3)
        // Opponent (1, 4)
        // we take the intersection of both list -> 1
        // so the index or position 1 on the board is a nash equilibrium
        // thus if we have multiple intersections, for example
        // Player   (1, 2)
        // Opponent (1, 2)
        // there are multiple nash equilibriums

        private fun calculateOpponentNash(): MutableList<Int> {

                var opponentNashStrategy = mutableListOf<Int>()

                if (game.board[0].second > game.board[1].second)
                    opponentNashStrategy.add(0)
                else
                    opponentNashStrategy.add(1)

                if (game.board[2].second > game.board[3].second)
                    opponentNashStrategy.add(2)
                else
                    opponentNashStrategy.add(3)

                return opponentNashStrategy
            }

        private fun calculatePlayerNash(): MutableList<Int> {

                var playerNashStrategy = mutableListOf<Int>()

                if (game.board[0].first > game.board[1].first)
                    playerNashStrategy.add(0)
                else
                    playerNashStrategy.add(1)

                if (game.board[2].first > game.board[3].first)
                    playerNashStrategy.add(2)
                else
                    playerNashStrategy.add(3)

                return playerNashStrategy
            }

        fun determineGameMode() {
            when (gameData.gameModeNew) {
                "Random" -> randomGame()
                "Greedy" -> greedyGame()
                "Cautious" -> cautiousGame()
                "Nash" -> nashGame()
            }
        }

        // highlight and return the end result pair
        fun calculateResult() : Pair<Int, Int> {
            if (playerAction == 'A' && opponentAction == 'A') {
                tvMatrix00?.setBackgroundResource(R.color.matrixResult)
                return board[0]
            }
            else if (playerAction == 'A' && opponentAction == 'B') {
                tvMatrix01?.setBackgroundResource(R.color.matrixResult)
                return board[1]
            }
            else if (playerAction == 'B' && opponentAction == 'A') {
                tvMatrix10?.setBackgroundResource(R.color.matrixResult)
                return board[2]
            }
            else if (playerAction == 'B' && opponentAction == 'B') {
                tvMatrix11?.setBackgroundResource(R.color.matrixResult)
                return board[3]
            }

            return Pair(0,0)
        }

        // fill matrix board with random integer pairs
        fun generateBoard() {
            (0 until textMatrixFields.size).forEach {
                board[it] = Pair(
                    (minMatrixValue..maxMatrixValue).random(),
                    (minMatrixValue..maxMatrixValue).random())
            }
        }

        // loop through all TextViews and set their pair values with the data
        // generated by  the generateBoard function
        fun displayBoard() {
            for (i in 0 until textMatrixFields.size)
                textMatrixFields[i]?.text = "${board[i].first} / ${board[i].second}"
        }
    }

    fun debugAll(p1: Int, p2: Int) {
        Log.d("XXXLOG", "total games: ${gameData.gamesPlayed}")
        Log.d("XXXLOG", "playerScore: ${gameData.playerScore}")
        Log.d("XXXLOG", "opponent: ${gameData.opponentScore}")
        Log.d("XXXLOG", "P ACTION: ${game.playerAction}")
        Log.d("XXXLOG", "O ACTION: ${game.opponentAction}")
        Log.d("XXXLOG", "P points: $p1")
        Log.d("XXXLOG", "O points: $p2")
    }

    // nash test function with predefined values from the lecture slide
    fun TestSingleNash() {
        game.board[0] = Pair(1,0)
        game.board[1] = Pair(5, -5)
        game.board[2] = Pair(5, 4)
        game.board[3] = Pair(-1, -5)
    }

    fun TestMultipleNash() {
        game.board[0] = Pair(-2, -2)
        game.board[1] = Pair(4, 0)
        game.board[2] = Pair(3, 4)
        game.board[3] = Pair(-2, -1)
    }
}