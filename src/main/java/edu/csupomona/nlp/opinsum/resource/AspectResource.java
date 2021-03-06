package edu.csupomona.nlp.opinsum.resource;

import edu.csupomona.nlp.opinsum.model.Device;
import edu.csupomona.nlp.opinsum.model.Sentence;
import edu.csupomona.nlp.opinsum.service.AspectService;
import edu.csupomona.nlp.opinsum.service.DeviceService;
import edu.csupomona.nlp.opinsum.service.SentenceService;
import edu.csupomona.nlp.opinsum.service.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Xing HU on 11/30/14.
 */
@Path("/aspect")
public class AspectResource {

    @Autowired
    AspectService aspectService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    SentenceService sentenceService;

    public AspectResource() {

    }

    @Path("/train")
    @GET
    public String loadAspectNGram() {
        // initialize table AspectSentenceCount and AspectNGram
        aspectService.initTraining();

        // create aspect list and train the classifier
        // aspect list
        List<String> aspects = new ArrayList<>();
        aspects.add("audio");
        aspects.add("battery");
        aspects.add("camera");
        aspects.add("network");
        aspects.add("os");
        aspects.add("price");
        aspects.add("screen");
        aspects.add("size");

        // aspect word list
        List<List<String>> aspectWords = new ArrayList<>();
        String[] audio = {"audio", "sound", "speaker", "microphone"};
        String[] battery = {"battery", "life", "charge"};
        String[] camera = {"camera", "shooter", "shutter", "aperture", "flash", "focus"};
        String[] network = {"network", "lte", "4g", "3g", "2g", "nfc", "wifi"};
        String[] os = {"os", "ios", "lollipop", "firmware", "android", "kitkat"};
        String[] price = {"price", "cost", "budget", "usd", "dollar"};
        String[] screen = {"screen", "display", "ppi", "resolution"};
        String[] size = {"size", "inch", "dimension"};
        aspectWords.add(Arrays.asList(audio));
        aspectWords.add(Arrays.asList(battery));
        aspectWords.add(Arrays.asList(camera));
        aspectWords.add(Arrays.asList(network));
        aspectWords.add(Arrays.asList(os));
        aspectWords.add(Arrays.asList(price));
        aspectWords.add(Arrays.asList(screen));
        aspectWords.add(Arrays.asList(size));

        // call training service
        aspectService.train(aspects, aspectWords);

        return "Aspect Identification prepared.";
    }

    @Path("/batchClassify/{productId}")
    @GET
    public String batchClassify(@PathParam("productId") String productId) {
        // obtain device info
        Device device = deviceService.findDeviceByProductId(productId);

        if (device != null) {
            // find all sentences for this device
            List<Sentence> sentences = sentenceService.findAllSentencesByDevice(device);

            if (sentences != null) {
                // classify these sentences
                aspectService.batchClassify(sentences);

                return "Batch processing " + device.getName() + " finished.";
            }
        }

        throw new NotFoundException(Response
                .status(Response.Status.NOT_FOUND)
                .entity("Device: " + productId + ", is not found")
                .build());
    }

}
