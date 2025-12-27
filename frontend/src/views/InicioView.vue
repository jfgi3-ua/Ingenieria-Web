<script setup lang="js">
	import imagenEstaticaSection1 from "@/assets/fotoBarraSection1Inicio.png"
	import squadGirlPhoto from "@/assets/squadGirlPhoto.png"
	import yogaImg from "@/assets/yogaImg.png"
	import gymImg from "@/assets/gymImg.png"
	import { ref } from "vue";

	const planes = ref(null);

	const scrollAPlanes = () => {
		planes.value.scrollIntoView({behavior: "smooth"});
	}
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
				<button @click="scrollAPlanes" class="ver-planes-btn">Ver planes</button>
			</div>
		</div>
		<div class="img-container">
			<img :src="imagenEstaticaSection1" alt="Imagen sección inicio">
		</div>
	</section>
	<section class="service-section">
		<div>
			<h3>Nuestros servicios</h3>
			<p class="section-txt">Todo lo que necesitas para alcanzar tus objetiuvos de fitness en un solo lugar</p>
			<div class="imgs-services">
				<div class="service-card">
					<img :src="squadGirlPhoto" alt="Tarjeta con imagen de servicio y descripcion">
					<div class="card-txt">
						<h4>Entrenamiento con pesas</h4>
						<p>Equipamiento profesional y zona de pesas libre para todos los niveles</p>
					</div>
				</div>
				<div class="service-card">
					<img :src="yogaImg" alt="Tarjeta con imagen de servicio y descripcion">
					<div class="card-txt">
						<h4>Clases grupales</h4>
						<p>Yoga, pilates, spinning, zumba y mucho más.</p>
						<p>Entrenamientos divertidos en grupo</p>
					</div>
				</div>
				<div class="service-card">
					<img :src="gymImg" alt="Tarjeta con imagen de servicio y descripcion">
					<div class="card-txt">
						<h4>Cardio</h4>
						<p>Zona de cardio moderna con cintas, bicicletas y elípticas de última generación</p>
					</div>
				</div>
				<div class="service-card">
					<img :src="imagenEstaticaSection1" alt="Tarjeta con imagen de servicio y descripcion">
					<div class="card-txt">
						<h4>Entrenamiento personal</h4>
						<p>Planes personalizados con entrenadores certificados para resultados óptimos</p>
					</div>
				</div>
			</div>
		</div>
	</section>
	<section ref="planes" class="prices-section">
		<div class="prices-container">
			<h3>Planes y precios</h3>
			<p class="section-txt">Elige el plan que mejor se adapte a tus necesidades y objetivos</p>
			<div class="card-prices-container">
				<div class="card-price">
					<h5>Básico</h5>
					<p>Perfecto para comenzar tu viaje fitness</p>
					<p class="price">€29/mes</p>
					<p>✔️ Acesso  al gimnasio</p>
					<p>✔️ Zona de pesas y cardio</p>
					<p>✔️ Vestuarios y duchas</p>
					<p>✔️ WiFi gratis</p>
					<button>Seleccionar plan</button>
				</div>
				<div class="card-price popular">
					<div class="popular-sticker">
						<p>Popular</p>
					</div>
					<h5>Premium</h5>
					<p>La opción más popular para resultados serios</p>
					<p class="price">€49/mes</p>
					<p>✔️ Todo lo del plan Básico</p>
					<p>✔️ Clases grupales ilimitadas</p>
					<p>✔️ Acceso 24/7</p>
					<p>✔️ Casillero personal</p>
					<p>✔️ Invita a un amigo gratis</p>
					<button>Seleccionar plan</button>
				</div>
				<div class="card-price">
					<h5>Élite</h5>
					<p>Para quienes buscan la excelencia</p>
					<p class="price">€79/mes</p>
					<p>Todo lo del plan Premium</p>
					<p>✔️ 4 sesiones de entrenamiento personal</p>
					<p>✔️ Plan nutricional personalizado</p>
					<p>✔️ Análisis corporal mensual</p>
					<p>✔️ Descuentos en suplementos</p>
					<button>Seleccionar plan</button>
				</div>
			</div>
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

	.service-section {
		background-color: #f4f5f6;
		height: 60vh;
		display: flex;
		flex-direction: column;
		align-items: center; /* Centra en columna*/
	}

	h3 {
		margin-top: 30px;
		margin-bottom: 15px;
		display: flex;
		justify-content: center;
		font-weight: lighter;
		font-size: x-large;
	}

	.section-txt {
		margin-bottom: 30px;
		font-size: medium;
		display: flex;
		justify-content: center;
	}

	.imgs-services {
		height: 40vh;
		width: 90vw;
		display: flex;
		flex-direction: row;
		justify-content: space-around;
		align-items: center;
	}

	.service-card > img{
		width: 20vw;
		border-top-left-radius: 10px;
		border-top-right-radius: 10px;
		margin-bottom: -4px;
	}

	.card-txt {
		height: 15vh;
		width: 20vw;
		background-color: white;
		border-bottom-left-radius: 10px;
		border-bottom-right-radius: 10px;
		box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);
	}

	.card-txt > h4 {
		padding-top: 5px;
		padding-left: 15px;
		font-size: large;
		font-weight: 400;
	}

	.card-txt > p {
		color: grey;
		padding-left: 15px;
		padding-top: 10px;
	}

	.prices-section {
		background-color: #f4f5f6;
		height: 80vh;
		display: flex;
		flex-direction: column;
		align-items: center; /* Centra en columna*/
	}

	.card-prices-container {
		display: flex;
		flex-direction: row;
		gap: 70px;
	}

	.card-price {
		border-radius: 10px;
		height: 380px;
		width: 300px;
		display: flex;
		flex-direction: column;
		box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);
	}

	.card-price > h5 {
		padding-top: 20px;
		padding-left: 25px;
		font-size: larger;
		font-weight: 500;
	}

	.card-price > p {
		margin-top: 10px;
		padding-left: 25px;
	}

	.price {
		font-size: xx-large;
	}

	.card-price > button {
		height: 40px;
		width: 170px;
		margin-top: 20px;
		align-self: center;
		cursor: pointer;
		background-color: #0092B8;
		border-radius: 5px;
		color: white;
	}

	.popular {
		color: white;
		background-color: #0092B8;
		position: relative;
	}

	.popular > button {
		background-color: white;
		color: #0092B8;

	}

	.popular-sticker {
		color: white;
		background-color: #1D293D;
		width: fit-content;
		padding: 7px;
		border-bottom-left-radius: 5px;
		border-top-right-radius: 5px;
		position: absolute;
		right: 0;
	}
</style>
