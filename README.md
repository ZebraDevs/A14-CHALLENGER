Exerciser for Android 14, linked to Nicola De Zolt's blog on [Nicola De Zolt's blog on A14](https://developer.zebra.com/blog/whats-new-androidtm-14-and-impact-zebra-developers)

_Please be aware that this application / sample is provided as-is for demonstration purposes without any guarantee of support_

Here is the app landing page; see below how each button maps to its respective topic in the blog and to the sample code.

![image](https://github.com/user-attachments/assets/17d4ecba-1a90-4e48-b553-067578cf043c)

and here is how buttons link to the sample code.

- [Typed Foreground Services](#fgstyperequired) => [onClickbtn_TYPED_FGS](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/app/src/main/java/com/ndzl/a14challenger/MainActivity2.kt#L84)
- [Minimum installable target API level](#mintargetapi) => see the specific module [oldApiTarget](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/oldApiTarget/build.gradle.kts#L13)

- [Credential Manager](#credman)
    * [SAVE Button](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/app/src/main/java/com/ndzl/a14challenger/MainActivity2.kt#L98C21-L98C49)
    * [RETRIEVE Button](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/app/src/main/java/com/ndzl/a14challenger/MainActivity2.kt#L129)
    * [GOOGLE FLOW](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/app/src/main/java/com/ndzl/a14challenger/MainActivity2.kt#L157)

- [Screenshot detection](#screendetect) => [TAKE SCREENSHOT BUTTON](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/app/src/main/java/com/ndzl/a14challenger/MainActivity2.kt#L240)
- [Restrictions to implicit and pending intents](#intentrestrict)
    * [MUTABLE PENDING INTENTS Button](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/app/src/main/java/com/ndzl/a14challenger/MainActivity2.kt#L309)
    * [BACKGROUND PENDING INTENTS Button](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/app/src/main/java/com/ndzl/a14challenger/MainActivity2.kt#L336)
 
- [Restrictions on background work](#bgwrestrict) => [BACKGROUND WORK Button](https://github.com/ZebraDevs/A14-CHALLENGER/blob/c75692173952a1eb1a1f177f30afd812e57839a0/app/src/main/java/com/ndzl/a14challenger/MainActivity2.kt#L360)

PERFORMANCE AND CORE FEATURES AFFECTING ALL APPS
- [Schedule exact alarms are denied by default](#exactalarms)
- [Queued Broadcasts](#queuedbroadcasts)

PERFORMANCE AND CORE FEATURES AFFECTING APPS TARGETING API LEVEL 34+

- [OpenJDK17](#openjdk17)
