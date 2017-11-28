package org.opencron.common.utils;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class MavenUtils {

    private String projectPath;

    public static MavenUtils get(ClassLoader classLoader){
        MavenUtils utils = new MavenUtils();
        String path = classLoader.getResource("").getPath();
        utils.projectPath = path.replaceFirst("/target/(.*)|/classes/","");
        return utils;
    }

    private Model getModel(String pom) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            return reader.read(new FileReader(pom));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("[opencron]:mavenUtils getModel error,please check " + pom);
    }

    private Model getParentModel(){
        return getModel("./pom.xml");
    }

    private Model getCurrentModel(){
        Model model = getParentModel();
        List<String> modules = model.getModules();
        for (String module:modules ) {
            if (projectPath.endsWith(module)) {
                String currentPom = "./".concat(module).concat("/pom.xml");
                model = getModel(currentPom);
                return model;
            }
        }
        return null;
    }

    public String getArtifactId()  {
        Model model = getCurrentModel();
        if (model == null) {
            return null;
        }
        return model.getArtifactId();
    }

    public String getArtifactVersion() {

        Model model = getCurrentModel();
        if (model!=null && model.getVersion()!=null) {
            return model.getVersion();
        }

        model = getParentModel();
        if (model!=null) {
            return model.getVersion();
        }
        return null;
    }

    public String getArtifact() {
        return getArtifactId().concat("-").concat(getArtifactVersion());
    }

    public String getName()  {
        Model model = getCurrentModel();
        return model.getName();
    }


    public static void main(String[] args) {
        MavenUtils mavenUtils = MavenUtils.get(Thread.currentThread().getContextClassLoader());
        System.out.println(mavenUtils.getArtifact() +"---> " + mavenUtils.getName());
    }



}
