<script setup lang="ts">

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

    </section>
</template>
<style>
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
        flex-direction: column;
        justify-content: center;
        gap: 10px;
    }

    .filters-section {
        background-color: #f4f5f6;
        display: flex;
        flex-direction: column;
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
    }

    .day-selection-button:active {
        background-color: #0092B8;
        color: white;
    }

    .day-selection-button:focus {
        background-color: #0092B8;
        color: white;
    }

    .activity-cards-section {
        background-color: #f4f5f6;
        height: 500px;
    }
</style>