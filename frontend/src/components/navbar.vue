<script setup lang="ts">
    import logoFitgymTransparente from "@/assets/logoFitgymTransparente.png"
    import { useAuthStore } from "@/stores/auth.store";
    import { ref, onMounted } from "vue"
    import { useRouter } from "vue-router";
    
    //Para poder pillar el elemento y meterle la clase dinamicamente
    const inicioBtnNav = ref();
    const reservaBtnNav = ref();
    const serviciosBtnNav = ref();
    const contactoBtnNav = ref();

    //Sesion del usuario
    const auth = useAuthStore();
    const router = useRouter();

    async function onLogout() {
        await auth.logout();
        await router.replace("/login");
    }

</script>
<template>
    <div class="general-container-navbar">
        <div class="fitgym-logo-container">
            <img :src="logoFitgymTransparente" alt="Imagen del logo de la marca">
            <p>FitGym</p>
        </div>
        <div class="buttons-container-navbar">
            <RouterLink class="nav-link" active-class="active-a" to="/inicio">Inicio</RouterLink>
            <RouterLink class="nav-link" active-class="active-a" to="/servicios">Servicios</RouterLink>
            <RouterLink class="nav-link" to="#">Mis reservas</RouterLink>
            <RouterLink class="nav-link" to="#">Mi cuenta</RouterLink>
        </div>
        <div v-if="auth.socio" class="user-buttons-container">
            <span>{{ auth.socio.nombre }}</span>
            <button class="logout-navbar-button" @click="onLogout">Cerrar sesión</button>
        </div>
        <div v-else class="user-buttons-container">
            <RouterLink class="register-navbar-button" to="/registro">Registrarse</RouterLink>
            <RouterLink class="login-navbar-button" to="/login">Iniciar sesión</RouterLink>
        </div>
    </div>
</template>
<style>
    * {
        padding: 0;
		margin: 0;
		border: 0;
    }

    .general-container-navbar{
        height: 30px;
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

    .nav-link {
        text-decoration: none;
        color: grey;
    }

    .nav-link.active-a {
        color: white;
        background-color: #0092B8;
        padding: 10px;
        border-radius: 5px;
    }

    .user-buttons-container {
        display: flex;
        flex-direction: row;
        gap: 20px;
        align-items: center;
    }

    .logout-navbar-button {
        all: unset;
        border-radius: 5px;
        height: 38px;
        width: 100px;
        background-color: rgb(244, 14, 14);
		color: white;
		cursor: pointer;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .logout-navbar-button:hover {
        background-color: rgb(184, 5, 5);
    }

    .register-navbar-button {
        all: unset;
        border-radius: 5px;
        height: 35px;
        width: 100px;
        background-color: #0092B8;
		color: white;
		cursor: pointer;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .login-navbar-button {
        all: unset;
        border-radius: 5px;
		border: 1px solid #0092B8;
		background-color: transparent;
		color: #0092B8;
        height: 35px;
        width: 100px;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
    }
</style>