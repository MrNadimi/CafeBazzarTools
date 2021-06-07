### CafeBazzarTools

Easy lib for [Cafe Bazzar](https://www.cafebazaar.ir) payments and ...

#### Config build.gradle

```` 
repositories {
      ...
      maven { url "https://www.jitpack.io" }
}
````
#### Add dependency on build.gradle


```` 
implementation 'com.github.MrNadimi:CafeBazzarTools:1.0.0'
````

#### How to use?

Easy! Just follow the examples

###### First: Create a Cafebazzar Object

````Java
CafeBazzar cafeBazzar = CafeBazzar.getInstance(this, base64EncodedPublicKey, new OnCafeBazzarListener() {
            @Override
            public void onCafeBazzarIsNotInslling(CafeBazzar cafeBazzar) {
                
            }

            @Override
            public void onInternetConnectionError(CafeBazzar cafeBazzar) {

            }

            @Override
            public void onStart(CafeBazzar cafeBazzar, IabResult result, boolean success, Exception ex) {

            }

            @Override
            public void onLoginStatus(CafeBazzar cafeBazzar, boolean userLogged, boolean success, Exception ex) {

            }

            @Override
            public void onProductsInventory(CafeBazzar cafeBazzar, List<SkuDetails> res, List<Purchase> purchases, boolean success, Exception ex) {

            }

            @Override
            public void onBuyInventory(CafeBazzar cafeBazzar, boolean success, Purchase purchase, Exception ex) {

            }

            @Override
            public void onSpendPurchase(CafeBazzar cafeBazzar, boolean success, Purchase purchase, Exception ex) {

            }
        });
```` 

##### Start: Now you can start the cafeBazzar object

````Java
cafeBazzar.start();
````
When you call this method, the result calls **onStart** method inside the listener object

##### Check user loging in status

When 'cafebazzar' started successfully, inside the **onStart** method we can check user login status

````Java
 @Override
 public void onStart(CafeBazzar cafeBazzar, IabResult result, boolean success, Exception ex) {
      if (success) {
           cafeBazzar.isUserLogged();
      }
}
````

##### Check Products:

````Java
List<String> skus = new ArrayList<>();
skus.add("Donate");
cafeBazzar.productsInventory(true , skus);
````

We can get the results inside the **onProductsInventory** method

##### Buying:

````Java
String palyload = payload or null ;
cafeBazzar.buy("Donate" , palyload);
````

We can get the results inside the **onBuyInventory** method

##### Spending the purchase:

````Java
 @Override
public void onBuyInventory(CafeBazzar cafeBazzar, boolean success, Purchase purchase, Exception ex) {
      cafeBazzar.spend(purchase);
}
````

We can get the results inside the **onSpendPurchase** method

#### Important: dispose the cafebazzar object inside the activity and implement onActivityResult method


````Java
 @Override
 protected void onDestroy() {
     cafeBazzar.dispose();
 }
    
````

and

````Java
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      if (CafeBazzar.onActivityResult(this, requestCode , resultCode , data)){
          return;
      }
      super.onActivityResult(requestCode, resultCode, data);
  }
````





