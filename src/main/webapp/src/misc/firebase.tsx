import { FirebaseError, initializeApp } from "firebase/app";
import { getAuth, signInWithEmailAndPassword } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const app = initializeApp({
    apiKey: "AIzaSyAIIndLbiZnw4W3LGXK6yPqudiVCwtRV8A",
    authDomain: "e-roster.firebaseapp.com",
    projectId: "e-roster",
    storageBucket: "e-roster.appspot.com",
    messagingSenderId: "1055789694297",
    appId: "1:1055789694297:web:2eeef790d7629d1452e048",
    measurementId: "G-JWZ13EDRQL"
});
const auth = getAuth(app);
const db = getFirestore(app);

const signIn = async (email: string, password: string) => {
    try {
        const cred = await signInWithEmailAndPassword(auth, email, password);
        const token = await cred.user.getIdToken();
        return token;
    } catch (e) {
        const err = e as FirebaseError;
        console.error("[signIn ERROR]", err.code);
    }
    return null;
}

const signOut = async () => await auth.signOut();

export {
    auth,
    db,
    signIn,
    signOut
};