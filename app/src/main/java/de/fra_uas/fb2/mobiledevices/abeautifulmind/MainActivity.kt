package de.fra_uas.fb2.mobiledevices.abeautifulmind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var tvRadioButtonHint: TextView? = null
    private var tvNumberGames: TextView? = null
    private val tvNumberGamesPreText = "Number of games played: "
    private var tvUserScore: TextView? = null
    private var tvOpponentScore: TextView? = null
    private var imageNash: ImageView? = null

    private var randomRadioButton: RadioButton? = null
    private var greedyRadioButton: RadioButton? = null
    private var cautiousRadioButton: RadioButton? = null
    private var nashRadioButton: RadioButton? = null

    private var generateGame: Button? = null
    private var startOver: Button? = null

    private val gameData: GameData = GameData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tvNumberGames = findViewById(R.id.tvNumberGames)
        tvUserScore = findViewById(R.id.tvUserScore)
        tvOpponentScore = findViewById(R.id.tvOpponentScore)

        imageNash = findViewById(R.id.imageNash)

        // on click listener im XML
        tvRadioButtonHint = findViewById(R.id.radioButtonHint)
        randomRadioButton = findViewById(R.id.radioRandom)
        greedyRadioButton = findViewById(R.id.radioGreedy)
        cautiousRadioButton = findViewById(R.id.radioCautious)
        nashRadioButton = findViewById(R.id.radioNash)

        generateGame = findViewById(R.id.buttonGenerateGame)
        startOver = findViewById(R.id.buttonStartOver)

        // message if user aborted game prematurely
        var statusFinished: Int = getIntent().getIntExtra("finished", -1)
        if (statusFinished == 0)
            CustomToast.makeText(this, "The game could not be finished!")
//        Toast.makeText(this, "The game could not be finished!", Toast.LENGTH_SHORT).show()

        // reset gameMode in Singleton class
        gameData.resetGameMode()

        displayAllScores()

        generateGame!!.setOnClickListener {
            if (gameData.gameModeNew != " ") {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            } else {
//                Toast.makeText(this@MainActivity, "Select an opponent first!", Toast.LENGTH_SHORT)
//                    .show()
                CustomToast.makeText(this, "Select an opponent first!")
            }
        }

        startOver!!.setOnClickListener {
            gameData.resetAll()
            displayAllScores()
        }
    }

    private fun displayAllScores() {
        tvNumberGames!!.text = "$tvNumberGamesPreText ${gameData.gamesPlayed}"
        tvUserScore!!.text = "${gameData.playerScore}"
        tvOpponentScore!!.text = "${gameData.opponentScore}"
    }


    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.radioRandom ->
                    if (checked) {
                        if (gameData.gameModeOld != "Random") {
                            imageNash?.visibility = View.INVISIBLE
                            gameData.resetAll()
                            displayAllScores()
                        }
                        gameData.gameModeNew = "Random"
                    }
                R.id.radioGreedy ->
                    if (checked) {
                        if (gameData.gameModeOld != "Greedy") {
                            imageNash?.visibility = View.INVISIBLE
                            gameData.resetAll()
                            displayAllScores()
                        }
                        gameData.gameModeNew = "Greedy"
                    }
                R.id.radioCautious ->
                    if (checked) {
                        if (gameData.gameModeOld != "Cautious") {
                            imageNash?.visibility = View.INVISIBLE
                            gameData.resetAll()
                            displayAllScores()
                        }
                        gameData.gameModeNew = "Cautious"
                    }
                R.id.radioNash ->
                    if (checked) {
                        if (gameData.gameModeOld != "Nash") {
                            imageNash?.visibility = View.VISIBLE
                            gameData.resetAll()
                            displayAllScores()
                        }
                        gameData.gameModeNew = "Nash"
                    }
            }
        }
    }

//    inner class SubmitHandler : View.OnClickListener {
//        override fun onClick(v: View?) {
//
//            if (randomRadioButton!!.isChecked)
//                gameData.gameMode = "Random"
//            else if (greedyRadioButton!!.isChecked)
//                gameData.gameMode = "Greedy"
//            else if (cautiousRadioButton!!.isChecked)
//                gameData.gameMode = "Cautious"
//            else if (nashRadioButton!!.isChecked)
//                gameData.gameMode = "Nash"
//            else {
//                Toast.makeText(this@MainActivity, "Select an opponent first!", Toast.LENGTH_SHORT)
//                    .show()
//                return
//            }
//
//            val intent = Intent(this@MainActivity, GameActivity::class.java)
//            startActivity(intent)
//        }
//    }
}
