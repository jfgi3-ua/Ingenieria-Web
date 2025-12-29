<script setup lang="ts">
    import yogaImg from "@/assets/yogaImg.png"
    import calendario from "@/assets/icons/calendario.png"
    import reloj from "@/assets/icons/reloj.png"
    import usuario from "@/assets/icons/simbolo-de-usuario-de-contorno-delgado.png"
    import type { Actividad } from "@/services/actividades"
    import { onMounted } from "vue"

    defineProps<{actividad: Actividad}>();

    //Funcion para posar de "2025-12-20" al día correspondiente (Lunes, Martes...)
    function obtenerDiaSemana(fechaStr: string): string {
        const fecha = new Date(fechaStr);
        const dia = fecha.toLocaleDateString("es-ES", {weekday: "long"});
        
        return dia.charAt(0).toUpperCase() + dia.slice(1).toLowerCase();
    }

    function calcularTiempoClase(horaIni: string, horaFin: string): number {
        const [hIni, mIni] = horaIni.split(":").map(Number);
        const [hFin, mFin] = horaFin.split(":").map(Number);

        const minutosIni = hIni! * 60 + mIni!;
        const minutosFin = hFin! * 60 + mFin!;

        return minutosFin - minutosIni;
    }

</script>
<template>
    <div class="general-card-container">
        <img :src="yogaImg" alt="Imagen de actividad asociada">
        <div class="card-info">
            <h4>{{ actividad.nombre }}</h4>
            <p>Sesión de yoga enfocada en el flujo de la respiración para todos los niveles</p>
            <div class="day-info">
                <img :src="calendario" alt="calendario">
                <p>{{ obtenerDiaSemana(actividad.fecha) }}</p>
            </div>
            <div class="time-info">
                <img :src="reloj" alt="reloj">
                <p>{{ actividad.horaIni.slice(0,5) }} ({{ calcularTiempoClase(actividad.horaIni, actividad.horaFin) }} min)</p>
            </div>
            <div class="instructor-info">
                <img :src="usuario" alt="usuario">
                <p v-if="actividad.monitor">{{ actividad.monitor }}</p>
                <p v-else>Por determinar</p>
            </div>
            <div class="sala-info">

            </div>
            <div class="progress-bar">

            </div>
            <button v-if="actividad.precioExtra == 0" class="card-button">Reservar tu plaza ></button>
            <button v-else class="card-button">Reservar tu plaza - €{{ actividad.precioExtra }} > </button>
        </div>
    </div>
</template>
<style>
    .general-card-container {
        height: 400px;
        width: 300px;
        margin-bottom: 30px;
        display: flex;
        flex-direction: column;
        background-color: white;
        border-bottom-left-radius: 10px;
        border-bottom-right-radius: 10px;
        box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);
    }

    .general-card-container > img {
        height: 150px;
        width: 300px;
        border-top-left-radius: 10px;
        border-top-right-radius: 10px;
    }

    .card-info {
        display: flex;
        flex-direction: column;
    }

    .card-info > h4 {
        margin-left: 25px;
        font-weight: 300;
        margin-top: 10px;
    }

    .card-info > p {
        font-size: small;
        color: grey;
        margin-left: 25px;
        margin-right: 25px;
        margin-top: 5px;
    }

    .day-info {
        display: flex;
        flex-direction: row;
        margin-left: 25px;
        margin-top: 15px;
        font-size: 14px;
        gap: 5px;
        color: grey;
    }

    .time-info {
        display: flex;
        flex-direction: row;
        margin-left: 25px;
        margin-top: 15px;
        font-size: 14px;
        gap: 5px;
        color: grey;
    }

    .instructor-info {
        display: flex;
        flex-direction: row;
        margin-left: 25px;
        margin-top: 15px;
        font-size: 14px;
        gap: 5px;
        color: grey;
    }

    .sala-info {
        display: flex;
        flex-direction: row;
        margin-left: 25px;
        margin-top: 15px;
        font-size: 14px;
        gap: 5px;
        color: grey;
    }

    .card-button {
        margin-top: 20px;
        margin-left: 25px;
        width: 250px;
        height: 40px;
		border-radius: 10px;
		background-color: #0092B8;
		color: white;
		cursor: pointer;
    }
</style>