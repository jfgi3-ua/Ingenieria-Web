<script setup lang="ts">
    import type { Actividad } from '@/types/actividad'
    import { useRoute } from 'vue-router'
    import { onMounted, nextTick, ref, computed } from 'vue'
    const route = useRoute()
    import yogaImg from "@/assets/yogaImg.png"
    import calendarioImg from "@/assets/icons/calendario.png"
    import relojImg from "@/assets/icons/reloj.png"
    import BarraDisponibles from '@/components/BarraDisponibles.vue'
    let actividadState: Actividad | undefined;
    const actividad = ref<Actividad>();

    onMounted(async () => {
        actividadState = (history.state as any)?.actividad;
        actividad.value = actividadState;
    });

    const diaSemana = computed(() => {
        if (!actividad.value){
            return '';
        }

        return obtenerDiaSemana(actividad.value.fecha);
    })

    const fechaSinAnyo = computed(() => {
        if (!actividad.value){
            return '';
        } 

        return obtenerFechaSinAnyo(actividad.value.fecha);
    })

    const duracionClase = computed(() => {
        if (!actividad.value){
            return 0;
        } 

        return calcularTiempoClase(actividad.value.horaIni, actividad.value.horaFin);
    })

    function calcularTiempoClase(horaIni: string, horaFin: string): number {
        const [hIni, mIni] = horaIni.split(":").map(Number);
        const [hFin, mFin] = horaFin.split(":").map(Number);

        const minutosIni = hIni! * 60 + mIni!;
        const minutosFin = hFin! * 60 + mFin!;

        return minutosFin - minutosIni;
    }

    //Funcion para posar de "2025-12-20" al día correspondiente (Lunes, Martes...)
    function obtenerDiaSemana(fechaStr: string): string {
        const fecha = new Date(fechaStr);
        const dia = fecha.toLocaleDateString("es-ES", {weekday: "long"});
        
        return dia.charAt(0).toUpperCase() + dia.slice(1).toLowerCase();
    }

    //Solo para sacar el dia y mes(quitamos el año que no nos importa)
    function obtenerFechaSinAnyo(fechaStr: string): string {
        const dia = fechaStr.slice(-2); //sacamos dia
        const mes = fechaStr.slice(5, 7);

        return `${dia}/${mes}`;
    }

</script>

<template>
    <div class="general-container-detail-activity">
        <div class="container-details-activity">
            <img class="img-details-activity" :src="yogaImg" alt="Imagen de actividad asociada">
            <h3 class="h3-details-activity">{{ actividad?.nombre }}</h3>
            <div class="horas-activity">
                <div class="div-horas-activity">
                    <p>Día</p>
                    <div>
                        <img :src="calendarioImg" alt="calendario">
                        <span style="margin-left: 5px;">{{ diaSemana }} - {{ fechaSinAnyo }}</span>
                    </div>
                </div>
                <div class="div-horas-activity">
                    <p>Hora inicio</p>
                    <div>
                        <img :src="relojImg" alt="calendario">
                        <span style="margin-left: 5px;">{{ actividad?.horaIni.slice(0, 5) }}</span>
                    </div>
                </div>
                <div class="div-horas-activity">
                    <p>Hora fin</p>
                    <div>
                        <img :src="relojImg" alt="calendario">
                        <span style="margin-left: 5px;">{{ actividad?.horaFin.slice(0, 5) }}</span>
                    </div>
                </div>
                <div class="div-horas-activity">
                    <p>Duración</p>
                    <div>
                        <img :src="relojImg" alt="calendario">
                        <span style="margin-left: 5px;">{{ duracionClase }} minutos</span>
                    </div>
                </div>
            </div>
            <div class="data-activity">
                <div class="div-monitor">
                    <p>Monitor: {{ actividad?.monitor }}</p>
                </div>
                <div class="div-plazas" v-if="actividad">
                    <BarraDisponibles :key="actividad.id" :actividad="actividad"/>
                    <div v-if="actividad?.precioExtra! > 0" class="div-info-msg">
                        <p>*Esta actividad requiere de un pago adicional</p>
                    </div>
                </div>
            </div>
            <button class="reserva-btn" :disabled="actividad?.disponibles == 0">Reservar actividad</button>
        </div>
    </div>
</template>
<style>
    html {
        margin: 0;
        padding: 0;
        border: none;
    }
    
    .general-container-detail-activity {
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        height: 93vh;
        width: auto;
        gap: 100px;
        background-color: #f4f5f6;
    }

    .container-details-activity {
        background-color: white;
        /*margin-top: -120px;*/
        border-bottom-left-radius: 10px;
        border-bottom-right-radius: 10px;
    }

    .img-details-activity{
        height: 300px;
        border-top-left-radius: 10px;
        border-top-right-radius: 10px;
    }

    .horas-activity {
        display: flex;
        flex-direction: row;
        justify-content: space-around;
    }

    .div-horas-activity {
        display: flex;
        flex-direction: column;
        font-size: 14px;
        color: grey;
    }

    .div-horas-activity > img {
        height: 15px;
        width: 15px;
    }

    .data-activity {
        margin-top: 15px;
        padding-left: 30px;
        font-size: medium;
        color: grey;
    }

    .div-plazas {
        margin-top: 20px;
        width: 500px;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .div-info-msg {
        color: red;
        font-size: small;
        margin-top: 5px;
    }

    .reserva-btn {
        height: 60px;
        font-weight: bold;
        cursor: pointer;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-self: center;
        justify-self: center;
        margin-top: 15px;
        margin-bottom: 15px;
    }

    .reserva-btn[disabled] {
        background-color: grey;
        color: rgb(243, 238, 238);
        cursor: not-allowed;
    }
</style>