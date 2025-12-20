<script setup lang="js">
	import imagenEstaticaSection1 from "@/assets/fotoBarraSection1Inicio.png"

</script>
<template>
	<section class="general-section">
		<div class="left-part-section-1">
			<div class="text-container">
				<p>Transforma tu cuerpo. Transforma tu vida.</p>
				<p>Únete a nuestra comunidad fitness y alcanza tus objetivos con entrenadores profesionales, equipamiento de última generación y un ambiente motivador</p>
			</div>
			<div class="buttons-container">
				<button class="reserva-btn">Reserva una clase</button>
				<button class="ver-planes-btn">Ver planes</button>
			</div>
		</div>
		<div class="img-container">
			<img :src="imagenEstaticaSection1" alt="Imagen sección inicio">
		</div>
	</section>
</template>
<style>
	* {
		padding: 0;
		margin: 0;
		border: 0;
	}

	.general-section {
		background-color: #1D293D;
		color: white;
		height: 40vh;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: center;

	}

	.left-part-section-1 {
		display: flex;
		flex-direction: column;
		width: 40vw;
	}

	.text-container {
		width: 80%;
		font-size: normal;
	}

	.text-container p:first-child {
		padding-bottom: 2vh;
	}

	.buttons-container {
		height: 50px;
		width: 70%;
		display: flex;
		margin-top: 20px;
	}

	.reserva-btn {
		width: 150px;
		border-radius: 10px;
		margin-right: 10px;
		background-color: #0092B8;
		color: white;
		cursor: pointer;
	}

	.ver-planes-btn {
		width: 120px;
		border-radius: 10px;
		border: 1px solid #0092B8;
		background-color: transparent;
		color: #0092B8;
		cursor: pointer;
	}

	.img-container {
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.img-container > img {
		height: 30vh;
		border-radius: 2vh;
	}
</style>