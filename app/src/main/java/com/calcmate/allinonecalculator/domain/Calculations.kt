package com.calcmate.allinonecalculator.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Period
import kotlin.math.*

object Numbers {
    fun format(value: Double, precision: Int = 8): String {
        if (!value.isFinite()) return "Invalid result"
        return BigDecimal.valueOf(value).setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString().replace("-0", "0")
    }
}

/** Safe recursive-descent evaluator. It never executes arbitrary code. */
class ExpressionCalculator(private val degrees: Boolean = false) {
    fun evaluate(input: String): Result<Double> = runCatching { Parser(input.replace("×", "*").replace("÷", "/")).parse().also { require(it.isFinite()) } }
    private inner class Parser(private val s: String) {
        var p = 0
        fun parse() = expression().also { skip(); require(p == s.length) }
        private fun skip() { while (p < s.length && s[p].isWhitespace()) p++ }
        private fun expression(): Double { var v = term(); while (true) { skip(); if (p >= s.length || (s[p] != '+' && s[p] != '-')) return v; val op = s[p++]; val n = term(); v = if (op == '+') v + n else v - n } }
        private fun term(): Double { var v = power(); while (true) { skip(); if (p >= s.length || (s[p] != '*' && s[p] != '/')) return v; val op = s[p++]; val n = power(); require(op != '/' || n != 0.0); v = if (op == '*') v * n else v / n } }
        private fun power(): Double { var v = unary(); skip(); if (p < s.length && s[p] == '^') { p++; v = v.pow(power()) }; return v }
        private fun unary(): Double { skip(); if (p < s.length && (s[p] == '+' || s[p] == '-')) { val negative = s[p++] == '-'; val v = unary(); return if (negative) -v else v }; return postfix() }
        private fun postfix(): Double { var v = atom(); while (true) { skip(); when { p < s.length && s[p] == '!' -> { p++; require(v >= 0 && v <= 170 && v % 1.0 == 0.0); v = (2..v.toInt()).fold(1.0) { a, n -> a * n } }; p < s.length && s[p] == '%' -> { p++; v /= 100 }; else -> return v } } }
        private fun atom(): Double { skip(); if (p < s.length && s[p] == '(') { p++; val v = expression(); skip(); require(p < s.length && s[p++] == ')'); return v }; val start = p; while (p < s.length && (s[p].isDigit() || s[p] == '.')) p++; if (p > start) return s.substring(start, p).toDouble(); val nameStart = p; while (p < s.length && s[p].isLetter()) p++; if (p > nameStart) { val name = s.substring(nameStart, p); val value = when (name) { "pi" -> Math.PI; "e" -> Math.E; else -> { skip(); require(p < s.length && s[p++] == '('); val arg = expression(); skip(); require(p < s.length && s[p++] == ')'); function(name, arg) } }; return value }; error("Expected value") }
        private fun function(name: String, x: Double): Double { val radians = if (degrees) Math.toRadians(x) else x; return when (name.lowercase()) { "sin" -> sin(radians); "cos" -> cos(radians); "tan" -> tan(radians); "asin" -> { val v = asin(x); if (degrees) Math.toDegrees(v) else v }; "acos" -> { val v = acos(x); if (degrees) Math.toDegrees(v) else v }; "atan" -> { val v = atan(x); if (degrees) Math.toDegrees(v) else v }; "ln" -> ln(x).also { require(it.isFinite()) }; "log" -> log10(x); "sqrt" -> sqrt(x); "abs" -> abs(x); "exp" -> exp(x); else -> error("Unknown function") }.also { require(it.isFinite()) } }
    }
}

data class LoanResult(val monthly: Double, val total: Double, val interest: Double)
data class DiscountResult(val saved: Double, val finalPrice: Double)
data class TipResult(val tip: Double, val total: Double, val perPerson: Double)
data class SavingsResult(val months: Int, val total: Double, val contributions: Double, val interest: Double)
data class FuelResult(val litres: Double, val cost: Double)
data class ElectricityResult(val kwh: Double, val cost: Double)
object CalculatorFormulas {
    fun percentOf(x: Double, y: Double) = x * y / 100
    fun increase(x: Double, p: Double) = x * (1 + p / 100)
    fun decrease(x: Double, p: Double) = x * (1 - p / 100)
    fun change(old: Double, new: Double) = if (old == 0.0) 0.0 else (new - old) / old * 100
    fun discount(price: Double, p: Double) = price * (1 - p / 100)
    fun discountSummary(price: Double, p: Double, tax: Double = 0.0): DiscountResult { require(price >= 0 && p in 0.0..100.0 && tax >= 0); val saved = price * p / 100; val after = price - saved; return DiscountResult(saved, after * (1 + tax / 100)) }
    fun loan(principal: Double, annualRate: Double, months: Int): LoanResult { require(principal >= 0 && annualRate >= 0 && months > 0); val r = annualRate / 1200; val payment = if (r == 0.0) principal / months else principal * r * (1 + r).pow(months) / ((1 + r).pow(months) - 1); val total = payment * months; return LoanResult(payment, total, total - principal) }
    fun compound(initial: Double, rate: Double, years: Double, frequency: Int, contribution: Double = 0.0): Double { require(initial >= 0 && rate >= 0 && years >= 0 && frequency > 0); val n = frequency * years; return initial * (1 + rate / 100 / frequency).pow(n) + if (rate == 0.0) contribution * n else contribution * ((1 + rate / 100 / frequency).pow(n) - 1) / (rate / 100 / frequency) }
    fun savings(target: Double, current: Double, monthly: Double, annualRate: Double = 0.0): SavingsResult { require(target >= current && monthly > 0 && annualRate >= 0); var balance = current; var contributions = 0.0; var months = 0; val r = annualRate / 1200; while (balance < target && months < 1200) { balance = balance * (1 + r) + monthly; contributions += monthly; months++ }; return SavingsResult(months, balance, contributions, balance - current - contributions) }
    fun profit(cost: Double, sale: Double) = sale - cost
    fun margin(cost: Double, sale: Double) = if (sale == 0.0) 0.0 else (sale - cost) / sale * 100
    fun markup(cost: Double, sale: Double) = if (cost == 0.0) 0.0 else (sale - cost) / cost * 100
    fun tip(bill: Double, percent: Double, people: Int): TipResult { require(bill >= 0 && percent >= 0 && people > 0); val tip = bill * percent / 100; return TipResult(tip, bill + tip, (bill + tip) / people) }
    fun fuel(distance: Double, economy: Double, price: Double): FuelResult { require(distance >= 0 && economy > 0 && price >= 0); val litres = distance / economy; return FuelResult(litres, litres * price) }
    fun electricity(watts: Double, hours: Double, days: Double, price: Double = 1.0): ElectricityResult { require(watts >= 0 && hours >= 0 && days >= 0 && price >= 0); val kwh = watts * hours * days / 1000; return ElectricityResult(kwh, kwh * price) }
    fun temperature(value: Double, from: String, to: String): Double { val c = when (from) { "F" -> (value - 32) * 5 / 9; "K" -> value - 273.15; else -> value }; return when (to) { "F" -> c * 9 / 5 + 32; "K" -> c + 273.15; else -> c } }
    fun storage(value: Double, from: String, to: String, binary: Boolean = false): Double { val base = if (binary) 1024.0 else 1000.0; val units = listOf("B", "KB", "MB", "GB", "TB"); return value * base.pow(units.indexOf(from).coerceAtLeast(0)) / base.pow(units.indexOf(to).coerceAtLeast(0)) }
    fun convert(value: Double, category: String, from: String, to: String): Double { if (from == to) return value; if (category == "Temperature") return temperature(value, from, to); val length = mapOf("mm" to .001, "cm" to .01, "m" to 1.0, "km" to 1000.0, "in" to .0254, "ft" to .3048, "mi" to 1609.344); val mass = mapOf("g" to .001, "kg" to 1.0, "oz" to .0283495, "lb" to .453592); val area = mapOf("m²" to 1.0, "ft²" to .092903, "km²" to 1_000_000.0); val volume = mapOf("L" to 1.0, "mL" to .001, "gal" to 3.78541); val speed = mapOf("m/s" to 1.0, "km/h" to .277778, "mph" to .44704); val time = mapOf("s" to 1.0, "min" to 60.0, "h" to 3600.0, "day" to 86400.0); val map = when (category) { "Mass" -> mass; "Area" -> area; "Volume" -> volume; "Speed" -> speed; "Time" -> time; else -> length }; return value * (map[from] ?: 1.0) / (map[to] ?: 1.0) }
    fun age(birth: LocalDate, now: LocalDate): Period = requireNotNull(if (birth <= now) Period.between(birth, now) else null)
}
