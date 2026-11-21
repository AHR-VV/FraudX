const voiceStatus = document.getElementById("voiceStatus");
const statusMessage = document.getElementById("statusMessage");
const orb = document.getElementById("orb");
const wave = document.getElementById("wave");
const voiceSelect = document.getElementById("voiceSelect");

let activeSpeech = null;
let availableVoices = [];
let speechKeepAlive;

// Load and display Indian voices with simple language names
function loadVoices() {
    const allVoices = speechSynthesis.getVoices();

    const languageNames = {
        "en-in": "English (India)",
        "hi-in": "हिन्दी (Hindi)",
        "te-in": "తెలుగు (Telugu)",
        "ta-in": "தமிழ் (Tamil)",
        "kn-in": "ಕನ್ನಡ (Kannada)",
        "ml-in": "മലയാളം (Malayalam)",
        "mr-in": "मराठी (Marathi)",
        "bn-in": "বাংলা (Bengali)",
        "gu-in": "ગુજરાતી (Gujarati)",
        "pa-in": "ਪੰਜਾਬੀ (Punjabi)"
    };

    const supported = allVoices.filter(voice =>
        languageNames[voice.lang.toLowerCase()]
    );

    // Prefer Natural/Online voices and keep one voice per language
    availableVoices = [];

    Object.keys(languageNames).forEach(lang => {
        const matches = supported.filter(
            voice => voice.lang.toLowerCase() === lang
        );

        if (matches.length === 0) return;

        matches.sort((a, b) => {
            const score = voice =>
                (voice.name.toLowerCase().includes("natural") ? 2 : 0) +
                (voice.name.toLowerCase().includes("online") ? 1 : 0);

            return score(b) - score(a);
        });

        availableVoices.push(matches[0]);
    });

    voiceSelect.innerHTML = "";

    availableVoices.forEach((voice, index) => {
        const option = document.createElement("option");

        option.value = index;
        option.textContent = languageNames[voice.lang.toLowerCase()];

        voiceSelect.appendChild(option);
    });

    // Default to Telugu if available, otherwise English
    const preferredVoice = availableVoices.findIndex(
        voice => voice.lang.toLowerCase() === "te-in"
    );

    if (preferredVoice !== -1) {
        voiceSelect.value = preferredVoice;
    } else {
        const englishVoice = availableVoices.findIndex(
            voice => voice.lang.toLowerCase() === "en-in"
        );

        if (englishVoice !== -1) {
            voiceSelect.value = englishVoice;
        }
    }
}


loadVoices();

speechSynthesis.onvoiceschanged = loadVoices;


// Get messages in selected language
function getMessages() {
    const selectedVoice = availableVoices[voiceSelect.value];
    const lang = selectedVoice ? selectedVoice.lang.toLowerCase() : "en";

    // Hindi
    if (lang.startsWith("hi")) {
        return {
            welcome: "नमस्ते! FraudX में आपका स्वागत है। मैं आपको ऑनलाइन सुरक्षित रहने में मदद करने के लिए यहाँ हूँ।",
            scam: "ज़रा सावधान रहिए! यह नंबर धोखाधड़ी वाला हो सकता है। कृपया अपना ओटीपी या बैंक की जानकारी किसी के साथ साझा न करें।",
            link: "रुकिए, इस लिंक पर क्लिक करने से पहले एक बार सोचिए। यह संदिग्ध लग रहा है। कृपया अपनी निजी जानकारी यहाँ न डालें।",
            safe: "यह लिंक अभी सुरक्षित लग रहा है। फिर भी, कोई भी निजी जानकारी साझा करने से पहले हमेशा जाँच कर लें।"
        };
    }

    // Telugu
    if (lang.startsWith("te")) {
        return {
            welcome: "నమస్కారం! FraudX కి స్వాగతం. ఆన్‌లైన్‌లో మీరు సురక్షితంగా ఉండేందుకు నేను మీకు సహాయం చేస్తాను.",
            scam: "ఒక్కసారి జాగ్రత్తగా ఉండండి! ఈ నంబర్ మోసానికి సంబంధించినది కావచ్చు. మీ ఓటీపీ లేదా బ్యాంక్ వివరాలను ఎవరితోనూ పంచుకోకండి.",
            link: "ఒక్కసారి ఆగండి! ఈ లింక్ అనుమానాస్పదంగా కనిపిస్తోంది. దీనిపై క్లిక్ చేయకండి, మీ వ్యక్తిగత వివరాలను కూడా ఇవ్వకండి.",
            safe: "ఈ లింక్ ప్రస్తుతం సురక్షితంగా కనిపిస్తోంది. అయినా, మీ వ్యక్తిగత వివరాలను పంచుకునే ముందు ఒకసారి తనిఖీ చేసుకోండి."
        };
    }

    // Tamil
    if (lang.startsWith("ta")) {
        return {
            welcome: "வணக்கம்! FraudX-க்கு வரவேற்கிறோம். இணையத்தில் நீங்கள் பாதுகாப்பாக இருக்க நான் உதவுகிறேன்.",
            scam: "கொஞ்சம் கவனமாக இருங்கள்! இந்த எண்ணில் மோசடி இருக்கலாம். உங்கள் OTP அல்லது வங்கி விவரங்களை யாரிடமும் பகிர வேண்டாம்.",
            link: "ஒரு நிமிடம்! இந்த இணைப்பு சந்தேகமாகத் தெரிகிறது. இதைக் கிளிக் செய்ய வேண்டாம். உங்கள் தனிப்பட்ட விவரங்களையும் உள்ளிட வேண்டாம்.",
            safe: "இந்த இணைப்பு இப்போது பாதுகாப்பாகத் தெரிகிறது. இருந்தாலும், உங்கள் தனிப்பட்ட விவரங்களைப் பகிரும் முன் சரிபார்த்துக் கொள்ளுங்கள்."
        };
    }

    // Kannada
    if (lang.startsWith("kn")) {
        return {
            welcome: "ನಮಸ್ಕಾರ! FraudX ಗೆ ಸ್ವಾಗತ. ನೀವು ಆನ್‌ಲೈನ್‌ನಲ್ಲಿ ಸುರಕ್ಷಿತವಾಗಿರಲು ನಾನು ಸಹಾಯ ಮಾಡುತ್ತೇನೆ.",
            scam: "ಸ್ವಲ್ಪ ಎಚ್ಚರಿಕೆಯಿಂದಿರಿ! ಈ ನಂಬರ್ ವಂಚನೆಗೆ ಸಂಬಂಧಿಸಿರಬಹುದು. ದಯವಿಟ್ಟು ನಿಮ್ಮ OTP ಅಥವಾ ಬ್ಯಾಂಕ್ ವಿವರಗಳನ್ನು ಯಾರೊಂದಿಗೂ ಹಂಚಿಕೊಳ್ಳಬೇಡಿ.",
            link: "ಒಂದು ಕ್ಷಣ ನಿಲ್ಲಿ! ಈ ಲಿಂಕ್ ಅನುಮಾನಾಸ್ಪದವಾಗಿ ಕಾಣುತ್ತಿದೆ. ಇದರ ಮೇಲೆ ಕ್ಲಿಕ್ ಮಾಡಬೇಡಿ ಮತ್ತು ನಿಮ್ಮ ವೈಯಕ್ತಿಕ ಮಾಹಿತಿಯನ್ನು ನೀಡಬೇಡಿ.",
            safe: "ಈ ಲಿಂಕ್ ಸದ್ಯಕ್ಕೆ ಸುರಕ್ಷಿತವಾಗಿ ಕಾಣುತ್ತಿದೆ. ಆದರೂ, ನಿಮ್ಮ ವೈಯಕ್ತಿಕ ಮಾಹಿತಿಯನ್ನು ಹಂಚಿಕೊಳ್ಳುವ ಮೊದಲು ಪರಿಶೀಲಿಸಿ."
        };
    }

    // Malayalam
    if (lang.startsWith("ml")) {
        return {
            welcome: "നമസ്കാരം! FraudX-ലേക്ക് സ്വാഗതം. ഓൺലൈനിൽ നിങ്ങൾ സുരക്ഷിതരായിരിക്കാൻ ഞാൻ സഹായിക്കാം.",
            scam: "ഒന്ന് ശ്രദ്ധിക്കൂ! ഈ നമ്പർ തട്ടിപ്പിനുള്ളതാകാം. നിങ്ങളുടെ OTP അല്ലെങ്കിൽ ബാങ്ക് വിവരങ്ങൾ ആരുമായും പങ്കിടരുത്.",
            link: "ഒരു നിമിഷം! ഈ ലിങ്ക് സംശയാസ്പദമായി തോന്നുന്നു. ഇതിൽ ക്ലിക്ക് ചെയ്യരുത്, നിങ്ങളുടെ സ്വകാര്യ വിവരങ്ങൾ നൽകുകയും ചെയ്യരുത്.",
            safe: "ഈ ലിങ്ക് ഇപ്പോൾ സുരക്ഷിതമാണെന്ന് തോന്നുന്നു. എങ്കിലും, സ്വകാര്യ വിവരങ്ങൾ പങ്കിടുന്നതിന് മുമ്പ് പരിശോധിക്കുക."
        };
    }

    // Marathi
    if (lang.startsWith("mr")) {
        return {
            welcome: "नमस्कार! FraudX मध्ये आपले स्वागत आहे. ऑनलाइन सुरक्षित राहण्यासाठी मी तुम्हाला मदत करेन.",
            scam: "जरा सावध राहा! हा नंबर फसवणुकीचा असू शकतो. कृपया तुमचा OTP किंवा बँकेची माहिती कोणालाही देऊ नका.",
            link: "थोडं थांबा! ही लिंक संशयास्पद वाटत आहे. त्यावर क्लिक करू नका आणि तुमची वैयक्तिक माहिती देऊ नका.",
            safe: "ही लिंक सध्या सुरक्षित वाटत आहे. तरीही, तुमची वैयक्तिक माहिती शेअर करण्यापूर्वी तपासून घ्या."
        };
    }

    // Bengali
    if (lang.startsWith("bn")) {
        return {
            welcome: "নমস্কার! FraudX-এ আপনাকে স্বাগতম। অনলাইনে নিরাপদ থাকতে আমি আপনাকে সাহায্য করব।",
            scam: "একটু সাবধান! এই নম্বরটি প্রতারণার সঙ্গে যুক্ত হতে পারে। আপনার OTP বা ব্যাঙ্কের তথ্য কাউকে দেবেন না।",
            link: "একটু থামুন! এই লিঙ্কটি সন্দেহজনক মনে হচ্ছে। এতে ক্লিক করবেন না বা ব্যক্তিগত তথ্য দেবেন না।",
            safe: "এই লিঙ্কটি আপাতত নিরাপদ বলে মনে হচ্ছে। তবুও, ব্যক্তিগত তথ্য দেওয়ার আগে যাচাই করে নিন।"
        };
    }

    // Gujarati
    if (lang.startsWith("gu")) {
        return {
            welcome: "નમસ્તે! FraudX માં તમારું સ્વાગત છે. ઓનલાઇન સુરક્ષિત રહેવામાં હું તમને મદદ કરીશ.",
            scam: "જરા સાવચેત રહો! આ નંબર છેતરપિંડી માટે હોઈ શકે છે. કૃપા કરીને તમારો OTP અથવા બેંકની વિગતો કોઈની સાથે શેર કરશો નહીં.",
            link: "જરા થોભો! આ લિંક શંકાસ્પદ લાગે છે. તેના પર ક્લિક કરશો નહીં અને તમારી વ્યક્તિગત માહિતી પણ આપશો નહીં.",
            safe: "આ લિંક હાલમાં સુરક્ષિત લાગે છે. તેમ છતાં, તમારી વ્યક્તિગત માહિતી શેર કરતા પહેલાં તપાસી લો."
        };
    }

    // Punjabi
    if (lang.startsWith("pa")) {
        return {
            welcome: "ਸਤ ਸ੍ਰੀ ਅਕਾਲ! FraudX ਵਿੱਚ ਤੁਹਾਡਾ ਸਵਾਗਤ ਹੈ। ਮੈਂ ਤੁਹਾਨੂੰ ਆਨਲਾਈਨ ਸੁਰੱਖਿਅਤ ਰਹਿਣ ਵਿੱਚ ਮਦਦ ਕਰਾਂਗਾ।",
            scam: "ਜ਼ਰਾ ਸਾਵਧਾਨ ਰਹੋ! ਇਹ ਨੰਬਰ ਧੋਖਾਧੜੀ ਵਾਲਾ ਹੋ ਸਕਦਾ ਹੈ। ਆਪਣਾ OTP ਜਾਂ ਬੈਂਕ ਦੀ ਜਾਣਕਾਰੀ ਕਿਸੇ ਨਾਲ ਵੀ ਸਾਂਝੀ ਨਾ ਕਰੋ।",
            link: "ਇੱਕ ਮਿੰਟ ਰੁਕੋ! ਇਹ ਲਿੰਕ ਸ਼ੱਕੀ ਲੱਗ ਰਿਹਾ ਹੈ। ਇਸ 'ਤੇ ਕਲਿੱਕ ਨਾ ਕਰੋ ਅਤੇ ਆਪਣੀ ਨਿੱਜੀ ਜਾਣਕਾਰੀ ਨਾ ਦਿਓ।",
            safe: "ਇਹ ਲਿੰਕ ਇਸ ਵੇਲੇ ਸੁਰੱਖਿਅਤ ਲੱਗ ਰਿਹਾ ਹੈ। ਫਿਰ ਵੀ, ਆਪਣੀ ਨਿੱਜੀ ਜਾਣਕਾਰੀ ਸਾਂਝੀ ਕਰਨ ਤੋਂ ਪਹਿਲਾਂ ਜਾਂਚ ਕਰ ਲਓ।"
        };
    }

    // English (India) fallback
    return {
        welcome: "Hey there! Welcome to FraudX. I'm here to help you stay safe while you're online.",
        scam: "Hey, please be careful! This number could be a scam. Don't share your OTP or banking details with anyone. Stay safe!",
        link: "Hold on a second! This link looks suspicious. It's better not to click on it or enter any personal details. Let's be safe.",
        safe: "This link looks safe for now. But just to be sure, always double-check before sharing any personal information."
    };
}

// Reset visual status
function resetVoiceUI(message = "Select another alert to listen.") {
    voiceStatus.textContent = "Voice assistant ready";
    statusMessage.textContent = message;

    orb.classList.remove("speaking");
    wave.classList.remove("active");
}
// Add natural pauses
function addNaturalPauses(message) {
    return message.replace(/([.!?।])\s+/g, "$1 ... ");
}

// Main speaking function
function speakFraudWarning(message) {

    if (!window.speechSynthesis) {
        voiceStatus.textContent = "Voice unavailable";
        statusMessage.textContent = "Your browser does not support voice alerts.";
        return;
    }

    clearInterval(speechKeepAlive);
    speechKeepAlive = null;

    speechSynthesis.cancel();
    activeSpeech = null;

    setTimeout(() => {

        const speech = new SpeechSynthesisUtterance(addNaturalPauses(message));

        const selectedVoice = availableVoices[voiceSelect.value];

        if (selectedVoice) {
            speech.voice = selectedVoice;
            speech.lang = selectedVoice.lang;
        }

        speech.rate = 0.88;
speech.pitch = 1.05;
speech.volume = 1;

        activeSpeech = speech;

        speech.onstart = function () {
            if (activeSpeech !== speech) return;

            voiceStatus.textContent = "FraudX is speaking...";
            statusMessage.textContent = "Listen carefully to this safety alert.";

            orb.classList.add("speaking");
            wave.classList.add("active");

            speechKeepAlive = setInterval(() => {
                if (activeSpeech === speech) {
                    speechSynthesis.resume();
                }
            }, 4000);
        };

        speech.onend = function () {
            if (activeSpeech !== speech) return;

            clearInterval(speechKeepAlive);
            speechKeepAlive = null;
            activeSpeech = null;

            resetVoiceUI("Select another alert to listen.");
        };

        speech.onerror = function (event) {
            if (activeSpeech !== speech) return;

            clearInterval(speechKeepAlive);
            speechKeepAlive = null;
            activeSpeech = null;

            console.error("Speech error:", event.error);

            resetVoiceUI("Voice stopped or interrupted.");
        };

        speechSynthesis.speak(speech);

    }, 200);
}
// Test selected language
function testSelectedVoice() {
    speakFraudWarning(getMessages().welcome);
}


// Test voice button
function testVoice() {
    speakFraudWarning(getMessages().welcome);
}


// Scam number alert
function warnScam() {
    speakFraudWarning(getMessages().scam);
}


// Suspicious link alert
function warnLink() {
    speakFraudWarning(getMessages().link);
}


// Safe link alert
function announceSafe() {
    speakFraudWarning(getMessages().safe);
}


// Stop voice
function stopVoice() {
    clearInterval(speechKeepAlive);
    speechKeepAlive = null;

    activeSpeech = null;

    speechSynthesis.cancel();

    resetVoiceUI("Voice alert stopped.");
}
// Backend integration entry point
function handleBackendAlert(type) {
    const messages = getMessages();

    if (type === "scam") {
        speakFraudWarning(messages.scam);
    }
    else if (type === "suspiciousLink") {
        speakFraudWarning(messages.link);
    }
    else if (type === "safe") {
        speakFraudWarning(messages.safe);
    }
}
