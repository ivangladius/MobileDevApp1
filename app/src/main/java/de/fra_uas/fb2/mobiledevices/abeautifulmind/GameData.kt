package de.fra_uas.fb2.mobiledevices.abeautifulmind

object GameData {
    var gameModeNew: String = " "
    var gameModeOld: String = " "
    var gamesPlayed: Int = 0
    var playerScore: Int = 0
    var opponentScore: Int = 0

    fun resetGameMode() {
        gameModeNew = " "
    }

    fun resetAll() {
        gamesPlayed = 0
        playerScore = 0
        opponentScore = 0
    }
}



