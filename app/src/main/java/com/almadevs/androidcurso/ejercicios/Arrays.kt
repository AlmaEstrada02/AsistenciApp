package com.almadevs.androidcurso.ejercicios

fun main(){
    //Indice o posición inicia desde el 0 (0-6) Tamaño del array es desde el 1 (1-7)
    val diasSemana= arrayOf("Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo")

    //imprimir el tamaño del arreglo
    println(diasSemana.size)

    //modificar valores
    /*diasSemana[1] = "Maartes"
    println(diasSemana[1])*/

    //Bucles para Arrays usando indices
    for(listado in diasSemana.indices){
        println(diasSemana[listado])
    }

    //Bucles para Arrays usando withIndex (se utiliza si queremos conocer el valor y la posición)
    for((listado, value) in diasSemana.withIndex()){
        println("La posición de $listado, contiene $value")
    }

    //Bucle para Arrays para conocer el valor
    for (lista in diasSemana){
        println("Ahora es $lista")
    }
}