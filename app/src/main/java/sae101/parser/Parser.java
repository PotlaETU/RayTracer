package sae101.parser;

import sae101.parser.light.*;
import sae101.parser.objects.Sphere;
import sae101.parser.objects.Triangle;
import sae101.parser.scene.*;
import sae101.triplet.*;

import java.io.*;
import java.util.InputMismatchException;


/**
 * The type Parser.
 */
public class Parser {

    /**
     * The constant sceneBuilder.
     */
    public static SceneBuilder sceneBuilder = new SceneBuilder();

    private File file;

    private BufferedReader in;

    /**
     * Instantiates a new Parser.
     *
     * @param path the path
     * @throws FileNotFoundException the file not found exception
     */
    public Parser(String path) throws FileNotFoundException {
        this.file = new File(path);
        this.in = new BufferedReader(new FileReader(file));
    }

    /**
     * Build scene.
     *
     * @return the scene
     * @throws IOException the io exception
     */
    public Scene build() throws IOException {
        sceneBuilder.setDiffuse(new Color(0,0,0));
        sceneBuilder.setSpecular(new Color(0,0,0));
        in.lines().forEach((line)->{
            String[] lineSplit = line.split(" ");
            switch (lineSplit[0]){

                case "size" -> sceneBuilder.setDimensions(Integer.parseInt(lineSplit[1]),Integer.parseInt(lineSplit[2]));

                case "output" -> sceneBuilder.setOutput(new File(lineSplit[1]));

                case "camera" -> sceneBuilder.setCamera(new Camera(new Vector(Double.parseDouble(lineSplit[1]),Double.parseDouble(lineSplit[2]),Double.parseDouble(lineSplit[3])),
                                new Vector(Double.parseDouble(lineSplit[4]),Double.parseDouble(lineSplit[5]),Double.parseDouble(lineSplit[6])),
                                new Vector(Double.parseDouble(lineSplit[7]),Double.parseDouble(lineSplit[8]),Double.parseDouble(lineSplit[9])),
                                Integer.parseInt(lineSplit[10]
                                )));

                case "ambient" -> sceneBuilder.setAmbient(new Color(new Triplet(Double.parseDouble(lineSplit[1]),Double.parseDouble(lineSplit[2]),Double.parseDouble(lineSplit[3]))));

                case "diffuse" -> {
                    if(sceneBuilder.getAmbient().getCoor().getX() + Double.parseDouble(lineSplit[1]) > 1 || sceneBuilder.getAmbient().getCoor().getY() + Double.parseDouble(lineSplit[2]) > 1 || sceneBuilder.getAmbient().getCoor().getZ() + Double.parseDouble(lineSplit[3]) > 1){
                        throw new InputMismatchException();
                    }
                    sceneBuilder.setDiffuse(new Color(new Triplet(Double.parseDouble(lineSplit[1]),Double.parseDouble(lineSplit[2]),Double.parseDouble(lineSplit[3]))));

                }

                case "specular" -> sceneBuilder.setSpecular(new Color(new Triplet(Double.parseDouble(lineSplit[1]),Double.parseDouble(lineSplit[2]),Double.parseDouble(lineSplit[3]))));

                case "shininess" -> sceneBuilder.setShininess(Integer.parseInt(lineSplit[1]));

                case "directional" -> {
                    DirectionalLigth ligth = new DirectionalLigth(
                            new Vector(Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2]), Integer.parseInt(lineSplit[3])),
                            new Color(new Triplet(Double.parseDouble(lineSplit[4]), Double.parseDouble(lineSplit[5]), Double.parseDouble(lineSplit[6])))
                    );
                    if(ligth.getColor().getCoor().getX() > 1 || ligth.getColor().getCoor().getY() > 1 || ligth.getColor().getCoor().getZ() > 1){
                        throw new InputMismatchException();
                    }
                    sceneBuilder.addLight(ligth);
                }

                case "point" -> {
                    PointLight ligth = new PointLight(
                            new Point(Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2]), Integer.parseInt(lineSplit[3])),
                            new Color(new Triplet(Double.parseDouble(lineSplit[4]), Double.parseDouble(lineSplit[5]), Double.parseDouble(lineSplit[6]))
                            ));
                    if(ligth.getColor().getCoor().getX() > 1 || ligth.getColor().getCoor().getY() > 1 || ligth.getColor().getCoor().getZ() > 1){
                        throw new InputMismatchException();
                    }
                    sceneBuilder.addLight(ligth);
                }

                case "maxverts" -> sceneBuilder.setMaxverts(Integer.parseInt(lineSplit[1]));

                case "vertex" -> sceneBuilder.addPoint(new Point(Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2]), Integer.parseInt(lineSplit[3])));

                case "tri" -> {
                    sceneBuilder.addObject(new Triangle(
                            sceneBuilder.getPoint(Integer.parseInt(lineSplit[1])).getCoor(),
                            sceneBuilder.getPoint(Integer.parseInt(lineSplit[2])).getCoor(),
                            sceneBuilder.getPoint(Integer.parseInt(lineSplit[3])).getCoor(),
                            sceneBuilder.getDiffuse().getCoor(),
                            sceneBuilder.getSpecular().getCoor(),
                            sceneBuilder.getShininess()
                    ));

                }

                case "sphere" -> sceneBuilder.addObject(
                        new Sphere(new Triplet(Integer.parseInt(lineSplit[1]),
                                Integer.parseInt(lineSplit[2]),
                                Integer.parseInt(lineSplit[3])),
                        Double.parseDouble(lineSplit[4]),
                        sceneBuilder.getDiffuse().getCoor(),
                        sceneBuilder.getSpecular().getCoor(),
                        sceneBuilder.getShininess()));
            }

        });
        in.close();
        return sceneBuilder.build();
    }
}
