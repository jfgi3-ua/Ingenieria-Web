<script setup lang="ts">
    import type { Actividad } from '@/types/actividad';
    import { computed } from 'vue';

    const props = defineProps<{ actividad: Actividad }>()

    const porcentajeDisponible = computed(() => {
        const { plazas, disponibles } = props.actividad

        if (plazas === 0){
            return 0
        }
        
        return ((plazas - disponibles) / plazas) * 100
    })

    const colorBarra = computed(() => {
        return props.actividad.disponibles <= 5 ? 'red' : '#0092B8'
    });
</script>
<template>
    <div class="progres-bar-container">
        <div class="data-container">
        <span v-if="actividad.disponibles === 0">Sin plazas</span>
        <span v-else-if="actividad.disponibles <= 5" class="warning-msg-plazas">¡Últimas plazas!</span>
        <span v-else>Plazas disponibles</span>

        <span>{{ actividad.disponibles }}/{{ actividad.plazas }}</span>
        </div>

        <div class="progres-bar">
        <div class="progres-bar-fill" :style="{ width: porcentajeDisponible + '%', backgroundColor: colorBarra }"/></div>
    </div>
</template>

<style>
    .progres-bar-container{
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    .data-container {
        display: flex;
        flex-direction: row;
        gap: 100px;
    }

    .data-container > span {
        font-size: small;
        color: grey;
    }

    .warning-msg-plazas {
        color: red;
    }

    .progres-bar {
        height: 7px;
        width: 250px;
        background-color: #E5E7EB;
        margin-top: 5px;
    }

    .progres-bar-fill {
        height: 100%;
        transition: width 0.3s ease;
    }

    .warning-msg-plazas {
        color: red;
    }

</style>