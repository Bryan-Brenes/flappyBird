package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;


import java.util.ArrayList;
import java.util.Random;

import sun.rmi.runtime.Log;

public class FlappyBirdGame extends ApplicationAdapter {
	private SpriteBatch batch;
    private Texture background;
    private Texture flappyBird1;
    private Texture flappyBird2;
    private Texture facilBtn;
    private Texture facilBtnPressed;
    private Texture dificilBtn;
    private Texture dificilBtnPressed;

    private Texture gameOverTexture;

    private ArrayList<Texture> birds;
    private ArrayList<Texture> topPipes;
    private ArrayList<Texture> bottomPipes;
    private ArrayList<Integer> alturaBottomPipes;
    private ArrayList<Integer> posicionX_pipes;
    private ArrayList<Texture> botonesDificil;
    private ArrayList<Texture> botonesFacil;

    private int estadoFlappy;

    private float GRAVEDAD = 10;

    private int posicionX_inicial;
    private int posicionY;

    private int posicionX_pipeInicial;
    private int pipesGap;
    private int pipesOffset;
    private int pipeVelocity;

    private int windowWidth;
    private int windowHeight;

    private int velocidadBird;
    private int velocidadMax;

    private Circle birdCircle;
    private ArrayList<Rectangle> topPipesRectangules;
    private ArrayList<Rectangle> bottomPipesRectangules;

    private ShapeRenderer shapeRenderer;

    private boolean isJuegoPerdido;

    private int puntaje;
    private BitmapFont puntajeBitmap;
    private int posicionX_puntaje;
    private int posicionY_puntaje;

    private Sound jumpingAudio;
    private Sound gameOverSound;

    private boolean estadoDificultad;
    private String textoDificultad;
    private BitmapFont dificultadBitmap;

    int anchoBotonesDificultad;

    int estadoBotonDificil;
    int estadoBotonFacil;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		birds = new ArrayList<Texture>();
		topPipes = new ArrayList<Texture>();
		bottomPipes = new ArrayList<Texture>();
		alturaBottomPipes = new ArrayList<Integer>();
		bottomPipesRectangules = new ArrayList<Rectangle>();
		topPipesRectangules = new ArrayList<Rectangle>();
		posicionX_pipes = new ArrayList<Integer>();
		botonesFacil = new ArrayList<Texture>();
		botonesDificil = new ArrayList<Texture>();

		background = new Texture("bg.png");
		flappyBird1 = new Texture("bird.png");
		flappyBird2 = new Texture("bird2.png");
		gameOverTexture = new Texture("game_over.png");
		facilBtn = new Texture("minusbutton.png");
		facilBtnPressed = new Texture("minusbuttonpressed.png");
		dificilBtn = new Texture("plusbutton.png");
		dificilBtnPressed = new Texture("plusbuttonpressed.png");

		botonesDificil.add(dificilBtn);
		botonesDificil.add(dificilBtnPressed);

		botonesFacil.add(facilBtn);
		botonesFacil.add(facilBtnPressed);

		poblarArreglosPipes();
		//poblarArreglosPipesRectangules();
		establecerAlturasPipesAleatorias();

		birds.add(flappyBird1);
		birds.add(flappyBird2);

		shapeRenderer = new ShapeRenderer();

		estadoFlappy = 0;
		posicionX_inicial = (Gdx.graphics.getWidth()/2) - (flappyBird1.getWidth()/2);
		posicionY = (Gdx.graphics.getHeight()/2) - (flappyBird1.getHeight()/2);
		//initialVelocity = 0f;
		//time = 0;
		//alturaMaximaSalto = 110;

		windowHeight = Gdx.graphics.getHeight();
		windowWidth = Gdx.graphics.getWidth();

		pipeVelocity = 8;
		posicionX_pipeInicial = (int) Math.round(windowWidth * 0.75);
		pipesOffset = 600;
		pipesGap = 450;

		velocidadBird = 0;
		velocidadMax = 50;

		birdCircle = new Circle();
		topPipesRectangules = new ArrayList<Rectangle>();
		bottomPipesRectangules = new ArrayList<Rectangle>();

		establecerPosicionesIniciales();

		isJuegoPerdido = false;

		puntaje = 0;
		puntajeBitmap = new BitmapFont();
		puntajeBitmap.setColor(Color.WHITE);
		puntajeBitmap.getData().setScale(10);
		posicionX_puntaje = 50;
		posicionY_puntaje = 150;

		jumpingAudio = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("over.mp3"));

		estadoDificultad = false; // false indica facil
		textoDificultad = "fácil";
		dificultadBitmap = new BitmapFont();
		dificultadBitmap.setColor(Color.WHITE);
		dificultadBitmap.getData().setScale(8);

		anchoBotonesDificultad = 250;
		estadoBotonFacil = 1;
		estadoBotonDificil = 0;

	}

	@Override
	public void render () {
		/*Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);*/

		if(estadoDificultad){
			GRAVEDAD = 11;
			velocidadMax = 50;
			pipesGap = 390;
			pipeVelocity = 12;
			textoDificultad = "difícil";

		} else {
			GRAVEDAD = 10;
			velocidadMax = 50;
			pipesGap = 450;
			pipeVelocity = 8;
			textoDificultad = "fácil";

		}

		posicionX_pipeInicial -= pipeVelocity;

		if(posicionX_pipes.get(0) < -bottomPipes.get(0).getWidth()){
			reciclarPipe();
			//posicionX_pipeInicial = pipesGap;
		}

		velocidadBird -= GRAVEDAD;

		long tiempoInicial = System.currentTimeMillis();

		estadoFlappy = estadoFlappy == 0 ? 1 : 0;

		if (Gdx.input.justTouched()){

		    // prueba de input para obtener las coordenadas del click
            int posX_input = Gdx.input.getX();
            int posY_input = Gdx.input.getY();
            System.out.println(String.format("x: %s, y: %s", String.valueOf(posX_input), String.valueOf(posY_input)));

            // verificando si presionó botones
			if(posX_input > 120 && posX_input < 371
					&& posY_input > 66 && posY_input < 316){
				estadoBotonFacil = 1;
				estadoBotonDificil = 0;
				estadoDificultad = false;
			} else if(posX_input > 716 && posX_input < 966
					&& posY_input > 66 && posY_input < 316){
				estadoBotonFacil = 0;
				estadoBotonDificil = 1;
				estadoDificultad = true;
			} else {
				jumpingAudio.play();
				if(!isJuegoPerdido){
					velocidadBird = velocidadMax;
				} else {
					isJuegoPerdido = false;
					puntaje = 0;
					velocidadBird = 0;
					posicionX_pipeInicial = (int) Math.round(windowWidth * 0.75);
					posicionX_pipes.clear();
					establecerPosicionesIniciales();

					alturaBottomPipes.clear();
					establecerAlturasPipesAleatorias();
					posicionY = (Gdx.graphics.getHeight()/2) - (flappyBird1.getHeight()/2);
				}
			}


		}

		if(verificarPasoEntrePipes()){
		    puntaje++;
        }


		batch.begin();
		// todos los batch.draw() que esten entre batch.begin() y batch.end() se dibujan juntos
		// el punto 0,0  es la esquina inferior izquierda
		// Gdx.graphics.getWidth()
		// Gdx.graphics.getHeight()

		// pintando el fondo - ir a manifest en Android y cambiar a portrait
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// pintando al pájaro, como se tienen dos sprites se tiene un booleano para ver cual se dibuja
		// para dar la sensación de aleteo

		topPipesRectangules.clear();
		bottomPipesRectangules.clear();

		if(isJuegoPerdido){
			batch.draw(gameOverTexture, (windowWidth / 2) - (gameOverTexture.getWidth() / 2),
					(windowHeight / 2));
		} else {

			// mostrar los pipes
			for (int i = 0; i < topPipes.size(); i++) {
				//int posY_pipe = rm.nextInt(windowHeight / 2);
				int posY_pipe = alturaBottomPipes.get(i) - bottomPipes.get(0).getHeight();
				int posX_pipe = posicionX_pipes.get(i);

				// dibujando pipe inferior
				batch.draw(bottomPipes.get(i), posX_pipe, posY_pipe);
				batch.draw(topPipes.get(i), posX_pipe, posY_pipe + pipesGap + topPipes.get(i).getHeight());

				// hay que dibujar los rectangulos para la detección de colisiones
			/*bottomPipesRectangules.get(i).set(posX_pipe, posY_pipe, bottomPipes.get(i).getWidth(), bottomPipes.get(i).getHeight());
			topPipesRectangules.get(i).set(posX_pipe, posY_pipe + pipesGap + topPipes.get(i).getHeight(), bottomPipes.get(i).getWidth(), bottomPipes.get(i).getHeight());*/
				bottomPipesRectangules.add(new Rectangle(posX_pipe, posY_pipe, bottomPipes.get(i).getWidth(), bottomPipes.get(i).getHeight()));
				topPipesRectangules.add(new Rectangle(posX_pipe, posY_pipe + pipesGap + topPipes.get(i).getHeight(), bottomPipes.get(i).getWidth(), bottomPipes.get(i).getHeight()));

			}

			puntajeBitmap.draw(batch, String.valueOf(puntaje), posicionX_puntaje, posicionY_puntaje);
			dificultadBitmap.draw(batch, textoDificultad, windowWidth - 400, posicionY_puntaje);
			batch.draw(botonesFacil.get(estadoBotonFacil), facilBtn.getHeight() / 4, windowHeight - facilBtn.getHeight() * 0.65f, anchoBotonesDificultad,anchoBotonesDificultad);
			batch.draw(botonesDificil.get(estadoBotonDificil), windowWidth - (3*dificilBtn.getWidth())/4, windowHeight - facilBtn.getHeight() * 0.65f, anchoBotonesDificultad, anchoBotonesDificultad);
			System.out.println(String.format("Posicion X: %s, Posicion Y: %s", String.valueOf(windowWidth - (3*dificilBtn.getWidth())/4),
					String.valueOf(windowHeight - facilBtn.getHeight() * 0.65f)));
			//System.out.println(String.format("AnchoVentana: %s, AltoVentana: %s", String.valueOf(windowWidth), String.valueOf(windowHeight)));
		}

		batch.draw(birds.get(estadoFlappy), posicionX_inicial, posicionY );

		batch.end();
		birdCircle.set(new Vector2(posicionX_inicial + (birds.get(0).getWidth() / 2),
				posicionY + (birds.get(0).getWidth() / 2)),
				birds.get(0).getWidth() / 2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);


		// aqui va el codigo para las colisiones
		for(int i = 0; i < topPipes.size(); i++){
			// aqui pintamos los rectangulos para los pipes con el shaper
			//Rectangle bottomRect = bottomPipesRectangules.get(i);
			//Rectangle topRect = topPipesRectangules.get(i);

			//shapeRenderer.rect(bottomRect.x, bottomRect.y, bottomRect.width, bottomRect.height);
			//shapeRenderer.rect(topRect.x, topRect.y, topRect.width, topRect.height);

			if(!isJuegoPerdido) {
				if (Intersector.overlaps(birdCircle, topPipesRectangules.get(i)) ||
						Intersector.overlaps(birdCircle, bottomPipesRectangules.get(i))) {
					// cambiar el estado del juego
					isJuegoPerdido = true;
					//gameOverSound.play();
					gameOverSound.setVolume(gameOverSound.play(),0.2f);
				}
			}
		}

		//shapeRenderer.end();

        posicionY += velocidadBird;

        if(posicionY < 0) {
            posicionY = 0;
            isJuegoPerdido = true;
        }

        reducirPosiciones();

		long tiempoFinal = System.currentTimeMillis();
		while ((tiempoFinal - tiempoInicial) < 50){
			tiempoFinal = System.currentTimeMillis();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		birds.get(0).dispose();
		birds.get(1).dispose();
		gameOverTexture.dispose();
		disposeArreglosPipes();
		jumpingAudio.dispose();
		gameOverSound.dispose();
	}

	private void poblarArreglosPipes(){
		for (int i = 0; i < 4; i++){
			Texture bottomPipe = new Texture("bottomtube.png");
			Texture topPipe = new Texture("toptube.png");

			bottomPipes.add(bottomPipe);
			topPipes.add(topPipe);
		}
	}

	private void poblarArreglosPipesRectangules(){
		for (int i = 0; i < 4; i++){
			Rectangle bottomPipe = new Rectangle(1,1,1,1);
			Rectangle topPipe = new Rectangle(1,1,1,1);

			bottomPipesRectangules.add(bottomPipe);
			topPipesRectangules.add(topPipe);
		}
	}

	private void disposeArreglosPipes(){
		for(Texture t : bottomPipes){
			t.dispose();
		}

		for(Texture t: topPipes){
			t.dispose();
		}
	}

	private void establecerAlturasPipesAleatorias(){
		int alturaMaxima = bottomPipes.get(0).getHeight() - pipesGap;
		Random rm = new Random();
		for(int i = 0; i < 4; i++){
			alturaBottomPipes.add(rm.nextInt(alturaMaxima) + 25);
		}
	}

	private void establecerPosicionesIniciales(){
		posicionX_pipes.add(posicionX_pipeInicial);
		for(int i = 1; i < 4; i++){
			posicionX_pipes.add(posicionX_pipeInicial + i * pipesOffset);
		}
	}

	private void reducirPosiciones(){
		ArrayList<Integer> nuevasPosiciones = new ArrayList<Integer>();
		for (int i = 0; i < posicionX_pipes.size(); i++){
			nuevasPosiciones.add(posicionX_pipes.get(i) - pipeVelocity);
		}
		posicionX_pipes = nuevasPosiciones;
	}

	private void reciclarPipe(){
		int alturaMaxima = bottomPipes.get(0).getHeight() - pipesGap;
		Random rm = new Random();

		Texture bottomPipe = bottomPipes.get(0);
		bottomPipes.remove(0);
		bottomPipes.add(bottomPipe);

		Texture topPipe = topPipes.get(0);
		topPipes.remove(0);
		topPipes.add(topPipe);

		alturaBottomPipes.remove(0);
		alturaBottomPipes.add(rm.nextInt(alturaMaxima) - 25);

		Integer posicion = posicionX_pipes.get(0);
		posicionX_pipes.remove(0);
		posicionX_pipes.add(posicionX_pipes.get(posicionX_pipes.size() - 1) + pipesOffset);
	}

	private boolean verificarPasoEntrePipes(){
	    for(int i = 0; i < posicionX_pipes.size(); i++){
	        if(posicionX_inicial > posicionX_pipes.get(i) - 5 && posicionX_inicial < posicionX_pipes.get(i) + 5 ){
	            return true;
            }
        }
	    return false;
    }
}
