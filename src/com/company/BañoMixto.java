package com.company;

import java.util.concurrent.Semaphore;

public class BañoMixto {
    private Semaphore mutex;
    private Semaphore capacidadHombres;
    private Semaphore capacidadMujeres;
    private int numHombresEnElBaño;
    private int numMujeresEnElBaño;
    private int capacidadMaxima;

    public BañoMixto(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.mutex = new Semaphore(1);
        this.capacidadHombres = new Semaphore(capacidadMaxima / 2);
        this.capacidadMujeres = new Semaphore(capacidadMaxima / 2);
        this.numHombresEnElBaño = 0;
        this.numMujeresEnElBaño = 0;
    }

    public void entrarBaño(String sexo) throws InterruptedException {
        mutex.acquire();
        if ((sexo.equals("Hombre") && numMujeresEnElBaño == 0) ||
                (sexo.equals("Mujer") && numHombresEnElBaño == 0)) {
            if (numHombresEnElBaño == 0 && numMujeresEnElBaño == 0) {
                // Si no hay nadie en el baño, está a la capacidad máxima
                capacidadHombres.release(capacidadMaxima / 2);
                capacidadMujeres.release(capacidadMaxima / 2);
            }
            if (sexo.equals("Hombre")) {
                capacidadHombres.acquire();
                numHombresEnElBaño++;
            } else {
                capacidadMujeres.acquire();
                numMujeresEnElBaño++;
            }
            System.out.println(sexo + " ha entrado al baño. Personas en el baño: " + obtenerTotalPersonasEnElBaño());
        } else {
            System.out.println(sexo + " está esperando fuera. Baño ocupado.");
        }
        mutex.release();
    }

    public void salirBaño(String sexo) throws InterruptedException {
        mutex.acquire();
        if (sexo.equals("Hombre")) {
            numHombresEnElBaño--;
            capacidadHombres.release();
        } else {
            numMujeresEnElBaño--;
            capacidadMujeres.release();
        }
        System.out.println(sexo + " ha salido del baño. Personas en el baño: " + obtenerTotalPersonasEnElBaño());
        if (numHombresEnElBaño == 0 && numMujeresEnElBaño == 0) {
            // Si no hay nadie en el baño, se libera la capacidad máxima
            capacidadHombres.drainPermits();
            capacidadMujeres.drainPermits();
        }
        mutex.release();
    }

    private int obtenerTotalPersonasEnElBaño() {
        return numHombresEnElBaño + numMujeresEnElBaño;
    }

    public static void main(String[] args) {
        BañoMixto baño = new BañoMixto(3); // Capacidad máxima del baño: 3

        // Personas entrando y saliendo del baño
        Thread persona1 = new Thread(() -> {
            try {
                baño.entrarBaño("Hombre");
                Thread.sleep(2000); // Tiempo en el baño
                baño.salirBaño("Hombre");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread persona2 = new Thread(() -> {
            try {
                baño.entrarBaño("Mujer");
                Thread.sleep(1500); // Tiempo en el baño
                baño.salirBaño("Mujer");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread persona3 = new Thread(() -> {
            try {
                baño.entrarBaño("Hombre");
                Thread.sleep(1000); // Tiempo en el baño
                baño.salirBaño("Hombre");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread persona4 = new Thread(() -> {
            try {
                baño.entrarBaño("Mujer");
                Thread.sleep(3000); // Tiempo en el baño
                baño.salirBaño("Mujer");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        persona1.start();
        persona2.start();
        persona3.start();
        persona4.start();
    }
}