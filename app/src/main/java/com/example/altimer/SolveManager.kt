package com.example.altimer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SolveManager {
    private const val SOLVES_KEY = "solves"

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
        Log.d("test", getSolves(context).toMutableList().toString())
    }
    fun addSolve(context: Context, solve: Solve) {
        val solves = getSolves(context).toMutableList()
        solves.add(solve) // Add the new solve
        saveSolves(context, solves)
        Log.d("test", "added" + getSolves(context).toMutableList().toString())
    }
    fun clearSolves(context: Context) {
        saveSolves(context, emptyList())
    }
    fun makeDNF(context: Context, position: Int) {
        val solves = getSolves(context).toMutableList()
        solves.get(position).penalty = "DNF"
        saveSolves(context, solves)
    }
    fun deleteSolve(context: Context, position: Int) {
        val solves = getSolves(context).toMutableList()
        solves.removeAt(position)
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
}
