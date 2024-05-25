# FundKeeper

## To samsung-campus.kz

Привет!

Это мой проект. Приложение для отслеживания доходов и расходов.

В проекте, обратите внимание на `google-services.json` и `apikey.properties`. Эти файлы являются необходимыми для работы приложения.
При желании, вы можете изменить их содержимое.

О том как это сделать, ниже, в уже более разработческом стиле)

## Development

Start by cloning this repo:
```
git clone https://github.com/MLGRussianXP/FundKeeper
```

Now, open it in Android Studio.

**Setup the Firebase**. Create a new project in Firebase Console and enable `Realtime Database` and `Authentications` apps.
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