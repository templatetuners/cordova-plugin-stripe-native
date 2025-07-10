# cordova-plugin-stripe-native

## Supported Platforms

- Android
- (iOS coming soon)

## Quick example

```
StripePlugin.init('pk_test_xxxxxx', function(res) {
  console.log('Stripe initialized:', res);
}, function(err){
  console.error('Error initializing Stripe:', err);
});
```

```
StripePlugin.payWithIntent(clientSecret, function(success){
  console.log("Payment succeeded:", success);
}, function(err){
  console.error("Payment failed:", err);
});  
```
