package com.example.altimer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.textclassifier.TextClassifierEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SolveManager {
    private const val SOLVES_KEY = "solves"
    private const val CUBE_TYPE_KEY = "cube_type"
    private const val ALG_TYPE_KEY = "alg_type"
    private const val INSP_TYPE_KEY = "insp_state"
    fun saveSolves(context: Context, solves: List<Solve>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(solves)
        editor.putString(SOLVES_KEY, json)
        editor.apply()
    }

    fun getSolves(context: Context): List<Solve> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(SOLVES_KEY, null)
        val type = object : TypeToken<List<Solve>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun editLastSolve(context: Context, solve: Solve) {
        val solves = getSolves(context).toMutableList()
        if (solves.isNotEmpty()) {
            solves.removeAt(solves.size - 1)
        }
        solves.add(solve)
        saveSolves(context, solves)

    }
    fun addSolve(context: Context, solve: Solve) {
        val solves = getSolves(context).toMutableList()
        solves.add(solve) // Add the new solve
        saveSolves(context, solves)

    }
    fun clearSolves(context: Context) {
        saveSolves(context, emptyList())
    }
    fun makeDNF(context: Context, position: Int, event:String) {
        val solves = getSolves(context).toMutableList()
        var pos = 0
        for (solve in solves) {
            if (solve.event == event) {
                pos++
            }
            if (pos == position) {
                solve.penalty = "DNF"
            }
        }
        saveSolves(context, solves)
    }
    fun deleteSolve(context: Context, scramble: String) {
        val solves = getSolves(context).toMutableList()
        var index = 0; var deleteindex = 0
        for (solve in solves) {
            index++
            if (solve.scramble == scramble) {
                deleteindex = index - 1
            }
        }
        solves.removeAt(deleteindex)
        saveSolves(context, solves)
    }
    fun eventHasSolve(context: Context, event: String) : Boolean {
        val solves = getSolves(context).toMutableList()
        for (solve in solves) {
            if (solve.event == event) {
                return true
            }
        }
        return false
    }
    fun isPlusTwo(context:Context, scramble: String) : Boolean {
        val solves = getSolves(context).toMutableList()
        for (solve in solves) {
            if (solve.scramble == scramble && solve.penalty == "+2") {
                return true
            }
            else {
                return false
            }
        }
        return false
    }
    fun hasPenalty(context:Context, scramble: String) : Boolean {
        val solves = getSolves(context).toMutableList()
        for (solve in solves) {
            if (solve.scramble == scramble) {
                if (solve.penalty != "0") {
                    return true
                }
            }
            else {
                return false
            }
        }
        return false
    }
    fun removePenalty(context:Context, scramble: String) {
        val solves = getSolves(context).toMutableList()
        for (solve in solves) {
            if (solve.scramble == scramble) {
                if (solve.penalty == "+2") {
                    solve.time -= 2.0f
                }
                solve.penalty = "0"
            }
        }
        saveSolves(context, solves)
    }
    fun givePlusTwo(context:Context, scramble: String) {
        val solves = getSolves(context).toMutableList()
        for (solve in solves) {
            if (solve.scramble == scramble) {
                solve.penalty = "+2"
                solve.time += 2.0f
            }
        }
        saveSolves(context, solves)
    }
    fun isSinglePB(context: Context, event: String, time: Float) : Boolean {

        val solves = getSolves(context).toMutableList()
        var fastestSingle = 0f
        for (solve in solves) {
            if (solve.event == event) {
                if (fastestSingle == 0f) {
                    fastestSingle = solve.time
                }
                else {
                    if (solve.time < fastestSingle) {
                        fastestSingle = solve.time
                    }
                }
            }
        }
        return fastestSingle == time
    }
    fun returnLastFiveSolves(context: Context, event: String) : ArrayList<Solve> {
        val solves = getSolveFromEvent(context, event).toMutableList(); val solvearray = ArrayList<Solve>()
        solves.takeLast(5)
        for (solve in solves) {
            solvearray.add(solve)
        }
        return solvearray
    }
    fun isAOFivePB(context: Context, event: String, average: Float): Boolean {
        var firstindex = 0; var lastindex = 5; var fastestaverage = 0f
        var solves = getSolveFromEvent(context, event).toMutableList()
        for (i in 1..solves.size - lastindex + 1) {
            val relevantSolves = solves.subList(firstindex, lastindex)
            val thataverage = calculateAverageOfFive(relevantSolves)
            if (fastestaverage == 0f) {
                fastestaverage = thataverage
            }
            else {
                if (thataverage < fastestaverage) {
                    fastestaverage = thataverage
                }
            }
        }
        return average == fastestaverage
    }
    fun saveCubeType(context: Context, cubeType: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(CUBE_TYPE_KEY, cubeType)
        editor.apply()
    }
    fun getCubeType(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        return sharedPreferences.getString(CUBE_TYPE_KEY, "") ?: ""
    }
    fun getSolveFromEvent(context: Context, event: String) : ArrayList<Solve> {
        val solves = getSolves(context).toMutableList()
        val eventsolves = ArrayList<Solve>()
        for (solve in solves) {
            if (solve.event == event) {
                eventsolves.add(solve)
            }
        }
        return eventsolves
    }
    fun saveAlgType(context: Context, algType: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(ALG_TYPE_KEY, algType)
        editor.apply()
    }
    fun getAlgType(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        return sharedPreferences.getString(ALG_TYPE_KEY, "") ?: ""
    }
    fun saveInspection(context: Context, inspection: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(INSP_TYPE_KEY, inspection)
        editor.apply()
    }
    fun getInspection(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        return sharedPreferences.getString(INSP_TYPE_KEY, "") ?: ""
    }
    fun calculateAverageOfFive(solves: List<Solve>) : Float {
        var averageOfFive = 0f; var eventSolves = ArrayList<Solve>()
        var shortest = 0f; var aofivelist = ArrayList<Float>()
        var longest = 0f

        for (solve in solves) {
            eventSolves.add(solve)
        }
        for (i in eventSolves.size - 5 until eventSolves.size) {
            if (eventSolves.get(i).penalty != "DNF") {
                aofivelist.add(eventSolves.get(i).time)
                if (shortest == 0f) {
                    shortest = eventSolves.get(i).time
                    longest = eventSolves.get(i).time
                }
                if (eventSolves.get(i).time < shortest) {
                    shortest = eventSolves.get(i).time
                }
                if (eventSolves.get(i).time > longest) {
                    longest = eventSolves.get(i).time
                }
            }
        }
        if (aofivelist.size == 4) {
            aofivelist.remove(shortest)
            for (time in aofivelist) {
                averageOfFive += time
            }
            averageOfFive /= 3
        }
        else {
            aofivelist.remove(shortest)
            aofivelist.remove(longest)
            for (time in aofivelist) {
                averageOfFive += time
            }
            averageOfFive /= 3
        }
        return averageOfFive
    }
}
