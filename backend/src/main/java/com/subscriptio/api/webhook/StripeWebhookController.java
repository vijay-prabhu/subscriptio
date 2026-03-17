package com.subscriptio.api.webhook;

import com.subscriptio.application.WebhookService;
import com.subscriptio.config.StripeProperties;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final WebhookService webhookService;
    private final StripeProperties stripeProperties;

    public StripeWebhookController(WebhookService webhookService, StripeProperties stripeProperties) {
        this.webhookService = webhookService;
        this.stripeProperties = stripeProperties;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeProperties.webhookSecret());
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        boolean processed = webhookService.processEvent(event.getId(), event.getType(), payload);
        if (processed) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing failed");
    }
}
