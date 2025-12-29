<script setup lang="ts">
    import ActivityCard from "@/components/ActivityCard.vue";
    import { listarActividades, type Actividad } from "@/services/actividades";
    import { onMounted, ref, computed } from "vue";

    const actividades = ref<Actividad[]>([]);
    const error = ref<string | null>(null);
    const paginaActual = ref(1);
    const actividadesPagina = 9;

    onMounted(async () => {
        try {
            actividades.value = await listarActividades();
            //console.log(actividades.value[0]);
        }
        catch (e) {
            error.value = e instanceof Error ? e.message : String(e);
        }
    })

    //Calcular cantidad de paginas
    const totalPaginas = computed(() => Math.ceil(actividades.value.length / actividadesPagina)); 
    
    const actividadesPaginadas = computed(() => {
        const inicio = (paginaActual.value - 1) * actividadesPagina;
        const fin = inicio + actividadesPagina;

        return actividades.value.slice(inicio,fin);
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
                <button class="day-selection-button">Todos</button>
                <button class="day-selection-button">Lunes</button>
                <button class="day-selection-button">Martes</button>
                <button class="day-selection-button">Miércoles</button>
                <button class="day-selection-button">Jueves</button>
                <button class="day-selection-button">Viernes</button>
                <button class="day-selection-button">Sábado</button>
            </div>
        </div>
    </section>

    <section class="activity-cards-section">
        <div class="activity-cards">
            <ActivityCard
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

.day-selection-button {
    height: 40px;
    width: 70px;
    background-color: #e7e9ed;
    border-radius: 5px;
}

.day-selection-button:active,
.day-selection-button:focus {
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