package com.mediatek.twoworlds.tv;

import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.KeymasterCertificateChain;
import android.security.keystore.KeyGenParameterSpec;
import java.io.ByteArrayInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Collection;

public class MtkTvKeymasterKeyAttestation {
    public static final String GOOGLE_ROOT_CERTIFICATE = "-----BEGIN CERTIFICATE-----\nMIIFYDCCA0igAwIBAgIJAOj6GWMU0voYMA0GCSqGSIb3DQEBCwUAMBsxGTAXBgNVBAUTEGY5MjAwOWU4NTNiNmIwNDUwHhcNMTYwNTI2MTYyODUyWhcNMjYwNTI0MTYyODUyWjAbMRkwFwYDVQQFExBmOTIwMDllODUzYjZiMDQ1MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAr7bHgiuxpwHsK7Qui8xUFmOr75gvMsd/dTEDDJdSSxtf6An7xyqpRR90PL2abxM1dEqlXnf2tqw1Ne4Xwl5jlRfdnJLmN0pTy/4lj4/7tv0Sk3iiKkypnEUtR6WfMgH0QZfKHM1+di+y9TFRtv6y//0rb+T+W8a9nsNL/ggjnar86461qO0rOs2cXjp3kOG1FEJ5MVmFmBGtnrKpa73XpXyTqRxB/M0n1n/W9nGqC4FSYa04T6N5RIZGBN2z2MT5IKGbFlbC8UrW0DxW7AYImQQcHtGl/m00QLVWutHQoVJYnFPlXTcHYvASLu+RhhsbDmxMgJJ0mcDpvsC4PjvB+TxywElgS70vE0XmLD+OJtvsBslHZvPBKCOdT0MS+tgSOIfga+z1Z1g7+DVagf7quvmag8jfPioyKvxnK/EgsTUVi2ghzq8wm27ud/mIM7AY2qEORR8Go3TVB4HzWQgpZrt3i5MIlCaY504LzSRiigHCzAPlHws+W0rB5N+er5/2pJKnfBSDiCiFAVtCLOZ7gLiMm0jhO2B6tUXHI/+MRPjy02i59lINMRRev56GKtcd9qO/0kUJWdZTdA2XoS82ixPvZtXQpUpuL12ab+9EaDK8Z4RHJYYfCT3Q5vNAXaiWQ+8PTWm2QgBR/bkwSWc+NpUFgNPN9PvQi8WEg5UmAGMCAwEAAaOBpjCBozAdBgNVHQ4EFgQUNmHhAHyIBQlRi0RsR/8aTMnqTxIwHwYDVR0jBBgwFoAUNmHhAHyIBQlRi0RsR/8aTMnqTxIwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAYYwQAYDVR0fBDkwNzA1oDOgMYYvaHR0cHM6Ly9hbmRyb2lkLmdvb2dsZWFwaXMuY29tL2F0dGVzdGF0aW9uL2NybC8wDQYJKoZIhvcNAQELBQADggIBACDIw41L3KlXG0aMiS//cqrG+EShHUGo8HNsw30W1kJtjn6UBwRM6jnmiwfBPb8VA91chb2vssAtX2zbTvqBJ9+LBPGCdw/E53Rbf86qhxKaiAHOjpvAy5Y3m00mqC0w/Zwvju1twb4vhLaJ5NkUJYsUS7rmJKHHBnETLi8GFqiEsqTWpG/6ibYCv7rYDBJDcR9W62BW9jfIoBQcxUCUJouMPH25lLNcDc1ssqvC2v7iUgI9LeoM1sNovqPmQUiG9rHli1vXxzCyaMTjwftkJLkf6724DFhuKug2jITV0QkXvaJWF4nUaHOTNA4uJU9WDvZLI1j83A+/xnAJUucIv/zGJ1AMH2boHqF8CY16LpsYgBt6tKxxWH00XcyDCdW2KlBCeqbQPcsFmWyWugxdcekhYsAWyoSf818NUsZdBWBaR/OukXrNLfkQ79IyZohZbvabO/X+MVT3rriAoKc8oE2Uws6DF+60PV7/WIPjNvXySdqspImSN78mflxDqwLqRBYkA3I75qppLGG9rp7UCdRjxMl8ZDBld+7yvHVgt1cVzJx9xnyGCC23UaicMDSXYrB4I4WHXPGjxhZuCuPBLTdOLU8YRvMYdEvYebWHMpvwGCF6bAx3JBpIeOQ1wDB5y0USicV3YgYGmi+NZfhA4URSh77Yd6uuJOJENRaNVTzk\n-----END CERTIFICATE-----";
    public static final String KEY_ALIAS = "test_key";
    private static final byte[] PRIVKEY_BYTES = hexToBytes("308204BE020100300D06092A864886F70D0101010500048204A8308204A40201000282010100E0473E8AB8F2284FEB9E742FF9748FA118ED98633C92F52AEB7A2EBE0D3BE60329BE766AD10EB6A515D0D2CFD9BEA7930F0C306537899F7958CD3E85B01F8818524D312584A94B251E3625B54141EDBFEE198808E1BB97FC7CB49B9EAAAF68E9C98D7D0EDC53BBC0FA0034356D6305FBBCC3C7001405386ABBC873CB0F3EF7425F3D33DF7B315AE036D2A0B66AFD47503B169BF36E3B5162515B715FDA83DEAF2C58AEB9ABFB3097C3CC9DD9DBE5EF296C176139028E8A671E63056D45F40188D2C4133490845DE52C2534E9C6B2478C07BDAE928823B62D066C7770F9F63F3DBA247F530844747BE7AAA85D853B8BD244ACEC3DE3C89AB46453AB4D24C3AC6902030100010282010037784776A5F17698F5AC960DFB83A1B67564E648BD0597CF8AB8087186F2669C27A9ECBDD480F0197A80D07309E6C6A96F925331E57F8B4AC6F4D45EDA45A23269C09FC428C07A4E6EDF738A15DEC97FABD2F2BB47A14F20EA72FCFE4C36E01ADA77BD137CD8D4DA10BB162E94A4662971F175F985FA188F056CB97EE2816F43AB9D3747612486CDA8C16196C30818A995EC85D38467791267B3BF21F273710A6925862576841C5B6712C12D4BD20A2F3299ADB7C135DA5E9515ABDA76E7CAF2A3BE80551D073B78BF1162C48AD2B7F4743A0238EE4D252F7D5E7E6533CCAE64CCB39360075A2FD1E034EC3AE5CE9C408CCBF0E25E4114021687B3DD4754AE8102818100F541884BC3737B2922D4119EF45E2DEE2CD4CBB75F45505A157AA5009F99C73A2DF0724AC46024306332EA8981776345465DC6DF1E0A6F140AFF3B7396E6A8994AC5DAA96873472FE37749D14EB3E075E629DBEB3583338A6F3649D0A2654A7A42FD9AB6BFA4AC4D481D390BB229B064BDC311CC1BE1B63189DA7C40CDECF2B102818100EA1A742DDB881CEDB7288C87E38D868DD7A409D15A43F445D5377A0B5731DDBFCA2DAF28A8E13CD5C0AFCEC3347D74A39E235A3CD9633F274DE2B94F92DF43833911D9E9F1CF58F27DE2E08FF45964C720D3EC2139DC7CAFC912953CDECB2F355A2E2C35A50FAD754CB3B23166424BA3B6E3112A2B898C38C5C15EDB2386933902818051828F1EC6FD996029901BAF1D7E337BA5F0AF27E984EAD895ACE62BD7DF4EE45A224089F2CC151AF3CD173FCE0474BCB04F386A2CDCC0E0036BA2419F54579262D47100BE931984A3EFA05BECF141574DC079B3A95C4A83E6C43F3214D6DF32D512DE198085E531E616B83FD7DD9D1F4E2607C3333D07C55D107D1D3893587102818100DB4FB50F50DE8EDB53FF34C8093188A0512867DA2CCA04897759E587C244010DAF8664D59E8083D16C164789301F67A9F078060D834A2ADBD367575B68A8A842C2B02A89B3F31FCCEC8A22FE395795C5C6C7422B4E5D74A1E9A8F30E7759B9FC2D639C1F15673E84E93A5EF1506F4315383C38D45CBD1B14048F4721DC82326102818100D8114593AF415FB612DBF1923710D54D07486205A76A3B43194968C0DFF1F11EF0F61A4A337D5FD3741BBC9640E447B8B6B6C47C3AC1204357D3B0C55BA9286BDA73F629296F5FA9146D8976357D3C751E75148696A40B74685C82CE30902D639D724FF24D5E2E9407EE34EDED2E3B4DF65AA9BCFEB6DF28D07BA6903F165768");

    private static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static void generateKeyPair(String algorithm, KeyGenParameterSpec spec) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm, "AndroidKeyStore");
        keyPairGenerator.initialize(spec);
        keyPairGenerator.generateKeyPair();
    }

    private static int verifyCertificateChain(Certificate[] certs) throws CertificateExpiredException, CertificateNotYetValidException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        for (int i = 1; i < certs.length; i++) {
            certs[i].checkValidity();
            if (i > 0) {
                PublicKey pubKey = certs[i].getPublicKey();
                certs[i - 1].verify(pubKey);
                if (i == certs.length - 1) {
                    certs[i].verify(pubKey);
                }
            }
        }
        if (Arrays.equals(((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(GOOGLE_ROOT_CERTIFICATE.getBytes()))).getEncoded(), certs[certs.length - 1].getEncoded())) {
            return 0;
        }
        return -1;
    }

    public static int testEcAttestationChain() throws Exception {
        generateKeyPair("EC", new KeyGenParameterSpec.Builder(KEY_ALIAS, 12).setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1")).setDigests(new String[]{"SHA-256"}).setAttestationChallenge("challenge".getBytes()).build());
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load((KeyStore.LoadStoreParameter) null);
        Certificate[] chain = keyStore.getCertificateChain(KEY_ALIAS);
        if (chain == null) {
            return -1;
        }
        try {
            return verifyCertificateChain(chain);
        } finally {
            keyStore.deleteEntry(KEY_ALIAS);
        }
    }

    private static Certificate generateCertificate(byte[] fakeUser) throws Exception {
        return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(fakeUser));
    }

    public static int testRsaAttestationChain() throws Exception {
        android.security.KeyStore mKeyStore = android.security.KeyStore.getInstance();
        mKeyStore.reset();
        mKeyStore.onUserPasswordChanged("password");
        mKeyStore.importKey(KEY_ALIAS, PRIVKEY_BYTES, -1, 1);
        KeymasterArguments args = new KeymasterArguments();
        args.addBytes(-1879047484, "challenge".getBytes());
        KeymasterCertificateChain outChain = new KeymasterCertificateChain();
        int errorCode = mKeyStore.attestKey(KEY_ALIAS, args, outChain);
        if (errorCode == 1) {
            Collection<byte[]> chain_list = outChain.getCertificates();
            if (chain_list.size() >= 2) {
                Certificate[] chain = new Certificate[chain_list.size()];
                int i = 0;
                for (byte[] data : chain_list) {
                    chain[i] = generateCertificate(data);
                    i++;
                }
                try {
                    return verifyCertificateChain(chain);
                } finally {
                    mKeyStore.delete(KEY_ALIAS);
                }
            } else {
                throw new ProviderException("Attestation certificate chain contained " + chain_list.size() + " entries. At least two are required.");
            }
        } else {
            throw new ProviderException("Failed to generate attestation certificate chain", android.security.KeyStore.getKeyStoreException(errorCode));
        }
    }
}
