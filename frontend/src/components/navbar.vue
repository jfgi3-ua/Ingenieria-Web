<script setup lang="js">
    import logoFitgymTransparente from "@/assets/logoFitgymTransparente.png"
    import { ref, onMounted } from "vue"
    
    //Para poder pillar el elemento y meterle la clase dinamicamente
    const inicioBtnNav = ref(null);
    const reservaBtnNav = ref(null);
    const serviciosBtnNav = ref(null);
    const contactoBtnNav = ref(null);

    //Obtenemos la ruta en que estamos y nos quedamos solo con el primer elemento -> Ruta principal
    onMounted(() => {
        var path = window.location.pathname;
        var mainPath = path.split("/");
        console.log(mainPath[1])
        switch(mainPath[1]){
            case "inicio":
                inicioBtnNav.value.classList.add("active-a");
                break;
            case "reserva":
                reservaBtnNav.value.classList.add("active-a");
                break;
            case "servicios":
                serviciosBtnNav.value.classList.add("active-a");
                break;
            case "contacto":
                contactoBtnNav.value.classList.add("active-a");
                break;
        }
    })

    
</script>
<template>
    <div class="general-container-navbar">
        <div class="fitgym-logo-container">
            <img :src="logoFitgymTransparente" alt="Imagen del logo de la marca">
            <p>FitGym</p>
        </div>
        <div class="buttons-container-navbar">
            <a ref="inicioBtnNav" href="#">Inicio</a>
            <a ref="reservaBtnNav" href="#">Reservar clase</a>
            <a ref="serviciosBtnNav" href="#">Servicios</a>
            <a ref="contactoBtnNav" href="#">Contacto</a>
        </div>
        <div class="user-buttons-container"></div>
    </div>
</template>
<style>
    * {
        padding: 0;
		margin: 0;
		border: 0;
    }

    .general-container-navbar{
        height: 70px;
        display: flex;
        flex-direction: row;
        position: relative;
        justify-content: space-around;
    }

    .fitgym-logo-container {
        width: 150px;
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: center;
        gap: 10px;
    }

    .fitgym-logo-container > img {
        height: 40px;
    }

    .buttons-container-navbar {
        width: 500px;
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: space-around;

    }

    .buttons-container-navbar > a.active-a {
        color: #428fdb;
    }

    .buttons-container-navbar > a {
        cursor: pointer;
        color: grey;
        text-decoration: none;
    }

    .buttons-container-navbar > a:hover {
        color: #0092B8;
    }

    .user-buttons-container {
        border: 1px solid black;
        width: 100px;
    }
</style>