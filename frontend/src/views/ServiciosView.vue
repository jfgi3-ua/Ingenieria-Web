<script setup lang="ts">
    import ActivityCard from "@/components/ActivityCard.vue";
    import { listarActividades } from "@/services/actividades";
    import type { Actividad } from "@/types/actividad";
    import { onMounted, ref, computed } from "vue";
    import { watch } from 'vue';

    const actividades = ref<Actividad[]>([]);
    const error = ref<string | null>(null);
    const paginaActual = ref(1);
    const actividadesPagina = 9;
    const diaFiltrado = ref<string |null>("Todos");
    const dias = ['Todos', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
    
    onMounted(async () => {
        try {
            actividades.value = await listarActividades();
            //console.log(actividades.value[0]?.disponibles);
        }
        catch (e) {
            error.value = e instanceof Error ? e.message : String(e);
        }
    })

    //Funcion para posar de "2025-12-20" al día correspondiente (Lunes, Martes...)
    function obtenerDiaSemana(fechaStr: string): string {
        const fecha = new Date(fechaStr);
        const dia = fecha.toLocaleDateString("es-ES", {weekday: "long"});
        
        return dia.charAt(0).toUpperCase() + dia.slice(1).toLowerCase();
    }

    const actividadesFiltradasDia = computed<Actividad[]>(() => {
        if(diaFiltrado.value === "Todos"){
            return actividades.value;
        } 
        return actividades.value.filter(a => obtenerDiaSemana(a.fecha) === diaFiltrado.value);
    });

    //Calcular cantidad de paginas
    const totalPaginas = computed(() => Math.ceil(actividadesFiltradasDia.value.length / actividadesPagina)); 
    
    //Poner otra vez desde la pag. 1 cuando cambiamos el filtro
    watch(diaFiltrado, () => {
        paginaActual.value = 1;
    });

    const actividadesPaginadas = computed(() => {
        const inicio = (paginaActual.value - 1) * actividadesPagina;
        const fin = inicio + actividadesPagina;

        return actividadesFiltradasDia.value.slice(inicio,fin).sort((a1, a2) => a1.fecha < a2.fecha ? -1 : 1);
    })

    function paginaSiguiente() {
        if(paginaActual.value < totalPaginas.value){
            paginaActual.value++;
        }
    }

    function paginaAnterior() {
        if(paginaActual.value > 1){
            paginaActual.value--;
        }
    }

    //Establecer día filtrado
    function seleccionarDia(dia: string) {
        diaFiltrado.value = dia;
        //console.log(dia);
    }

    
</script>

<template>
    <section class="first-section">
        <div class="first-section-static-info">
            <p>Reserva tu clase grupal</p>
            <p>Elige entre nuestra variedad de clases grupales y reserva tu plaza. Todas las clases están dirigidas por instructores certificados.</p>
        </div>
    </section>

    <section class="filters-section">
        <div class="day-container">
            <p>Día de la semana</p>

            <div class="day-selection-container">
                <label class="day-option" v-for="dia in dias" :key="dia" :class="{ active: diaFiltrado === dia }">
                    <input type="radio" name="day" :value="dia" v-model="diaFiltrado" hidden />
                    <span @click="seleccionarDia(dia)">{{ dia }}</span>
                </label>
            </div>
        </div>
    </section>

    <section class="activity-cards-section">
        <div class="activity-cards">
            <div v-if="actividadesPaginadas.length == 0">
                <p>No hay actividades programadas para esta fecha...</p>
            </div>
            <ActivityCard
                v-else
                v-for="actividad in actividadesPaginadas"
                :key="actividad.id"
                :actividad="actividad"
            />
        </div>
    </section>

    <div class="pagination">
        <button class="pagination-btn" @click="paginaAnterior" :disabled="paginaActual === 1">
            ← Anterior
        </button>

        <span>Página {{ paginaActual }} de {{ totalPaginas }}</span>

        <button class="pagination-btn" @click="paginaSiguiente" :disabled="paginaActual === totalPaginas">
            Siguiente →
        </button>
    </div>

</template>

<style>
html {
    background-color: #f4f5f6;
}

.first-section {
    height: 200px;
    background-color: #0092B8;
    color: white;
    display: flex;
    flex-direction: column;
}

.first-section-static-info {
    width: 570px;
    margin-left: 200px;
    margin-top: 60px;
    display: flex;
    background-color: #0092B8;
    flex-direction: column;
    justify-content: center;
    gap: 10px;
}

.first-section-static-info > p {
    background-color: #0092B8;
}

.filters-section {
    background-color: #f4f5f6;
    display: flex;
    flex-direction: column;
    height: 150px;
}

.day-container {
    margin-left: 200px;
    height: 100px;
    color: #364153;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.day-container > p {
    margin-top: 10px;
}

.day-selection-container {
    width: 550px;
    height: auto;
    display: flex;
    flex-direction: row;
    justify-content: space-between
}

.day-option {
    cursor: pointer;
}

.day-option.active span {
    background-color: #0092B8;
    color: white;
}

.day-option input {
    display: none;
}

.day-option span {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 40px;
    width: 70px;
    background-color: #e7e9ed;
    border-radius: 5px;
    cursor: pointer;
    font-size: 15px;
}

.day-option input:checked + span {
    background-color: #0092B8;
    color: white;
}

.activity-cards-section {
    background-color: #f4f5f6;
    height: auto;
    display: flex;
    justify-content: center;
    align-self: center;
}

.activity-cards {
    /*margin-left: 200px;*/
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 60px;
}

.pagination {
    margin: 40px 0;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
}

.pagination-btn {
    padding: 10px 20px;
    background-color: #0092B8;
    color: white;
    border-radius: 6px;
    font-weight: 500;
    cursor: pointer;
}

.pagination-btn:disabled {
    background-color: #c7cbd1;
    cursor: not-allowed;
}

</style>