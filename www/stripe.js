var exec = require('cordova/exec');

module.exports = {
  init: function (publishableKey, success, error) {
    exec(success, error, 'StripePlugin', 'init', [publishableKey]);
  },

  payWithIntent: function (clientSecret, success, error) {
    exec(success, error, 'StripePlugin', 'payWithIntent', [clientSecret]);
  },

  setupPaymentSheet: function (clientSecret, success, error) {
    exec(success, error, 'StripePlugin', 'setupPaymentSheet', [clientSecret]);
  }
};