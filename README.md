# FundKeeper

## Development

Start by cloning this repo (_it's private right now_):
```
git clone https://github.com/MLGRussianXP/FundKeeper
```

Now, open it in Android Studio.

**Setup the Firebase**. Create a new project in Firebase Console and enable `Realtime Database` and `Authentication` apps.
In Authentication app, open Settings tab and:
- Enable **only** `Email/Password` sign-in method.
- Disable `Email enumeration protection` in User actions.

Then, download the `google-services.json` file and paste it into `app` directory.

**Get the ExchangeRate-API key**. Go to https://www.exchangerate-api.com/ and get your key.
Create a `apikey.properties` file in the root directory (where the `gradlew` is) and paste the key there:
```
EXCHANGERATE_KEY="YOUR_KEY_HERE"
```

### You're ready to go!

## GitHub actions

To make GitHub actions work, you need to add your `google-services.json` files to the repository.
In `Settings -> Secrets and variables -> Actions -> Secrets` add a new secret named `GOOGLE_SERVICES_JSON`.
Put the base64 encoded `google-services.json` file there.
