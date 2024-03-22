package com.almadevs.androidcurso.ejercicios

fun main(){
    mutableList()
}

fun mutableList(){
    val diasSemana:MutableList<String> = mutableListOf("Lunes","Martes","Miercoles","Jueves","Viernes","Sabado")
    //diasSemana.add("domingo")//Se añade el campo adicional en la ultima posición
    diasSemana.add(0,"Domingo")  //añadir el valor en la posición seleccionada con e
    println(diasSemana)

    diasSemana.forEach { println(it) }  //Realizar un for sobre la lista
}

fun inmutableList(){
    val readOnly:List<String> = listOf("Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo")

    //println(readOnly.size)
    println(readOnly)
    println(readOnly[3])
    println(readOnly.last()) //para mostrar la ultima posición
    println(readOnly.first()) //para mostrar la primera posición

    //Realizar un filtro con alguna caracteristica
    val filtro = readOnly.filter { it.contains("a")}
    println("los días que tienen la letra a son: $filtro")

    //Iterar
    readOnly.forEach{ weekDay -> println(weekDay)} //Imprimir los valores de la lista con salto de linea
}
