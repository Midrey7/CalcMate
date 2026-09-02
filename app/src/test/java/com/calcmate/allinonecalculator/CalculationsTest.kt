package com.calcmate.allinonecalculator
import com.calcmate.allinonecalculator.domain.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class CalculationsTest {
 @Test fun arithmetic(){ assertEquals(14.0,ExpressionCalculator().evaluate("2+3*4").getOrThrow(),0.0001); assertTrue(ExpressionCalculator().evaluate("2/0").isFailure) }
 @Test fun scientific(){ assertEquals(1.0,ExpressionCalculator(true).evaluate("sin(90)^2+cos(90)^2").getOrThrow(),.0001); assertEquals(17.1415,ExpressionCalculator().evaluate("sqrt(144)+log(100)+pi").getOrThrow(),.001); assertTrue(ExpressionCalculator().evaluate("ln(-1)").isFailure) }
 @Test fun percentages(){ assertEquals(25.0,CalculatorFormulas.percentOf(50.0,50.0),.001); assertEquals(20.0,CalculatorFormulas.change(100.0,120.0),.001) }
 @Test fun loan(){ val r=CalculatorFormulas.loan(10000.0,6.0,12); assertEquals(860.66,r.monthly,.1); assertTrue(r.interest>300) }
 @Test fun compound(){ assertEquals(1100.0,CalculatorFormulas.compound(1000.0,10.0,1.0,1),.01) }
 @Test fun business(){ assertEquals(40.0,CalculatorFormulas.margin(60.0,100.0),.001); assertEquals(66.666,CalculatorFormulas.markup(60.0,100.0),.01) }
 @Test fun conversions(){ assertEquals(1.0,CalculatorFormulas.convert(100.0,"Length","cm","m"),.001) }
 @Test fun dates(){ val p=CalculatorFormulas.age(LocalDate.of(2000,2,29),LocalDate.of(2024,2,28)); assertEquals(23,p.years) }
 @Test fun utilities(){ assertEquals(6.0,CalculatorFormulas.electricity(100.0,2.0,30.0,1.0).kwh,.001); assertEquals(20.0,CalculatorFormulas.fuel(100.0,10.0,2.0).cost,.001); assertEquals(0.0,CalculatorFormulas.temperature(32.0,"F","C"),.001); assertEquals(1_000_000.0,CalculatorFormulas.storage(1.0,"MB","B"),.001) }
 @Test fun matricesAndGraphs(){ val m=Matrix(listOf(listOf(1.0,2.0),listOf(3.0,4.0))); assertEquals(-2.0,m.determinant(),.001); assertEquals(1.0,m.times(m.inverse()).rows[0][0],.001); assertEquals(9,GraphSampler.sample("x^2",-1.0,1.0,9).size) }
 @Test fun dateOperations(){ val d=DateCalculations.difference(LocalDate.of(2024,1,31),LocalDate.of(2024,3,1)); assertEquals(30,d.totalDays); assertEquals(LocalDate.of(2025,2,28),DateCalculations.add(LocalDate.of(2024,2,29),years=1)); assertEquals(1,DateCalculations.daysUntilBirthday(LocalDate.of(2000,12,31),LocalDate.of(2024,12,30))) }
}
