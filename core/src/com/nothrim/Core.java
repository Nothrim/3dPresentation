package com.nothrim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

public class Core extends ApplicationAdapter implements InputProcessor {
    private PerspectiveCamera cam;
    private ModelBatch modelBatch;
    private ModelBatch shadowBatch;
    private Environment environment;
    private AssetManager assets;
    private Array<ModelInstance> instances = new Array<>();
    private DirectionalShadowLight shadowLight;
    private ColorAttribute part1Color = new ColorAttribute(ColorAttribute.Diffuse, 0.6f, 0.1f, 0.1f, 1f);
    private ColorAttribute part2Color = new ColorAttribute(ColorAttribute.Diffuse, 0.6f, 0.6f, 0.6f, 1f);
    private ColorAttribute part3Color = new ColorAttribute(ColorAttribute.Diffuse, 0.8f, 0.1f, 0.1f, 1f);
    private ColorAttribute part4Color = new ColorAttribute(ColorAttribute.Diffuse, 0.1f, 0.8f, 0.1f, 1f);
    private float cameraSpeed = 10f;
    private boolean shiftPressed = false;

    @Override
    public void create() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0f, 1f, .6f, 1f));
        environment.add((shadowLight = new DirectionalShadowLight(1024, 1024, 60f, 60f, .1f, 150f))
                .set(1f, 1f, 1f, 0, -10f, -35f));
        environment.shadowMap = shadowLight;
        shadowBatch = new ModelBatch(new DepthShaderProvider());
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(9f, 9f, 120f);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        Gdx.input.setInputProcessor(new InputMultiplexer(new CameraInputController(cam), this));
        assets = new AssetManager();
        assets.load("Pumpkin_D.jpg", Texture.class);
        assets.load("Pumpkin_N.jpg", Texture.class);
        assets.load("Pumpkin_S.jpg", Texture.class);
        assets.load("shovel.g3db", Model.class);
        assets.load("aluminum.jpg", Texture.class);
        assets.load("map_glossy.jpg", Texture.class);
        assets.finishLoading();
        Model model = assets.get("shovel.g3db", Model.class);
        ModelInstance modelInstance = new ModelInstance(model);
        modelInstance.materials.forEach(material -> material.set(
                new TextureAttribute(TextureAttribute.Diffuse, assets.get("aluminum.jpg", Texture.class)),
                new TextureAttribute(TextureAttribute.Specular, assets.get("map_glossy.jpg", Texture.class))));
        modelInstance.materials.get(0).set(part1Color);
        modelInstance.materials.get(1).set(part2Color);
        modelInstance.materials.get(2).set(part3Color);
        modelInstance.materials.get(3).set(part4Color);
        cam.lookAt(modelInstance.transform.getTranslation(new Vector3()));
        instances.add(modelInstance);
        Material pumpkinMaterial = new Material(
                new TextureAttribute(TextureAttribute.Diffuse, assets.get("Pumpkin_D.jpg", Texture.class)),
                new TextureAttribute(TextureAttribute.Normal, assets.get("Pumpkin_N.jpg", Texture.class)),
                new TextureAttribute(TextureAttribute.Specular, assets.get("Pumpkin_S.jpg", Texture.class)));
        ModelBuilder modelBuilder = new ModelBuilder();

        model = modelBuilder.createBox(100f, 1f, 100f, pumpkinMaterial
                ,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates |
                        VertexAttributes.Usage.Tangent);
        instances.add(new ModelInstance(model));
        instances.get(1).transform.setTranslation(0, -30, 0);
        setupStartingAttributes();
    }


    @Override
    public void render() {
        shadowLight.begin(Vector3.Zero, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());

        shadowBatch.render(instances);

        shadowBatch.end();
        shadowLight.end();
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        cam.rotateAround(instances.get(0).transform.getTranslation(new Vector3()), new Vector3(0, 1, 0), (Gdx.graphics.getDeltaTime() * cameraSpeed));
        cam.update();
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SHIFT_LEFT) {
            shiftPressed = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.W) {
            shadowLight.direction.set(shadowLight.direction.x, shadowLight.direction.y + 0.05f, shadowLight.direction.z);
        }
        if (keycode == Input.Keys.S) {
            shadowLight.direction.set(shadowLight.direction.x, shadowLight.direction.y - 0.05f, shadowLight.direction.z);
        }

        if (keycode == Input.Keys.A) {
            shadowLight.direction.set(shadowLight.direction.x + 0.05f, shadowLight.direction.y, shadowLight.direction.z);
        }
        if (keycode == Input.Keys.D) {
            shadowLight.direction.set(shadowLight.direction.x - 0.05f, shadowLight.direction.y, shadowLight.direction.z);
        }
        if (keycode == Input.Keys.R) {
            restart();
        }
        if (keycode == Input.Keys.NUM_1) {
            ColorPicker.paint(part1Color);
        }
        if (keycode == Input.Keys.NUM_2) {
            ColorPicker.paint(part2Color);
        }
        if (keycode == Input.Keys.NUM_3) {
            ColorPicker.paint(part3Color);
        }
        if (keycode == Input.Keys.NUM_4) {
            ColorPicker.paint(part4Color);
        }
        if (keycode == Input.Keys.Q) {
            cameraSpeed--;
        }
        if (keycode == Input.Keys.E) {
            cameraSpeed++;
        }
        if (keycode == Input.Keys.C) {
            instances.get(0).materials.forEach(m -> {
                m.remove(TextureAttribute.Diffuse);
                m.remove(TextureAttribute.Specular);
            });
        }
        if (keycode == Input.Keys.E && shiftPressed) {
            cameraSpeed += 100;
        }
        if (keycode == Input.Keys.Q && shiftPressed) {
            cameraSpeed -= 100;
        }
        if (keycode == Input.Keys.SHIFT_LEFT) {
            shiftPressed = false;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private void setupStartingAttributes() {
        StartingAttributes.shadowLightDirection = shadowLight.direction.cpy();
        StartingAttributes.shovelModel = instances.get(0).copy();
        StartingAttributes.floorModel = instances.get(1).copy();
    }

    private void restart() {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(9f, 9f, 120f);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        cameraSpeed = 10f;
        Gdx.input.setInputProcessor(new InputMultiplexer(new CameraInputController(cam), this));
        instances= new Array<>();
        instances.add(StartingAttributes.shovelModel.copy());
        instances.add(StartingAttributes.floorModel.copy());
        ModelInstance modelInstance = instances.get(0);
        ColorPicker.clear();
        part1Color = new ColorAttribute(ColorAttribute.Diffuse, 0.6f, 0.1f, 0.1f, 1f);
        part2Color = new ColorAttribute(ColorAttribute.Diffuse, 0.6f, 0.6f, 0.6f, 1f);
        part3Color = new ColorAttribute(ColorAttribute.Diffuse, 0.8f, 0.1f, 0.1f, 1f);
        part4Color = new ColorAttribute(ColorAttribute.Diffuse, 0.1f, 0.8f, 0.1f, 1f);
        modelInstance.materials.get(0).set(part1Color);
        modelInstance.materials.get(1).set(part2Color);
        modelInstance.materials.get(2).set(part3Color);
        modelInstance.materials.get(3).set(part4Color);
        shadowLight.direction.set(StartingAttributes.shadowLightDirection);
    }

}
