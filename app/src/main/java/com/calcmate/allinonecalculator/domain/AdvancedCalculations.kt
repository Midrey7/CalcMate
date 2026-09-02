package com.calcmate.allinonecalculator.domain

import kotlin.math.abs

data class Matrix(val rows: List<List<Double>>) {
    init { require(rows.isNotEmpty() && rows.all { it.size == rows.first().size }) }
    val rowCount get() = rows.size
    val columnCount get() = rows.first().size
    operator fun times(other: Matrix): Matrix { require(columnCount == other.rowCount); return Matrix(List(rowCount) { r -> List(other.columnCount) { c -> (0 until columnCount).sumOf { rows[r][it] * other.rows[it][c] } } }) }
    fun determinant(): Double { require(rowCount == columnCount); if (rowCount == 1) return rows[0][0]; if (rowCount == 2) return rows[0][0] * rows[1][1] - rows[0][1] * rows[1][0]; return rows[0].indices.sumOf { c -> (if (c % 2 == 0) 1 else -1) * rows[0][c] * minor(0, c).determinant() } }
    private fun minor(row: Int, column: Int) = Matrix(rows.filterIndexed { i, _ -> i != row }.map { values -> values.filterIndexed { j, _ -> j != column } })
    fun inverse(): Matrix { require(rowCount == columnCount); val n = rowCount; val a = Array(n) { r -> DoubleArray(n * 2) { c -> if (c < n) rows[r][c] else if (c - n == r) 1.0 else 0.0 } }; for (column in 0 until n) { val pivot = (column until n).maxByOrNull { abs(a[it][column]) }!!; require(abs(a[pivot][column]) > 1e-12); val temp = a[column]; a[column] = a[pivot]; a[pivot] = temp; val divisor = a[column][column]; for (c in 0 until n * 2) a[column][c] /= divisor; for (r in 0 until n) if (r != column) { val factor = a[r][column]; for (c in 0 until n * 2) a[r][c] -= factor * a[column][c] } }; return Matrix(List(n) { r -> List(n) { c -> a[r][c + n] } }) }
}

object GraphSampler { fun sample(expression: String, start: Double = -10.0, end: Double = 10.0, points: Int = 101): List<Pair<Double, Double>> { require(points >= 2 && end > start); val evaluator = ExpressionCalculator(); return (0 until points).map { i -> val x = start + (end - start) * i / (points - 1); val y = evaluator.evaluate(expression.replace("x", "($x)")).getOrNull(); x to (y ?: Double.NaN) }.filter { it.second.isFinite() } } }
