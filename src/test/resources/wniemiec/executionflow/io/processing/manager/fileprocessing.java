package wniemiec.executionflow.io.processing.manager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class fileprocessing {
@wniemiec.executionflow.runtime.CollectCalls public static int countTotalArguments(Object... args) {
int i = 2;
int k;int _15bc61334f990459c74317f4b345be64=0;
int _ac4366cef80f0198c3dbaea69de7b8bb=0;
int _e8708f2fa601793801655972f8915554=0;
int _72849cf1bcc71ccbe2467892347ba5e3=0;
k=0;
while (k<args.length) {int _f24c4bc065456dc223839c0e606c77e6=0;
int _2dc89f95406c12d992d80c6bcc396991=0;
int _1ad05287cd15f64e25e6d9c9e45fd705=0;
int _66ceac4d8c1030d02fffa6d1280a4d4b=0;
k++;
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {continue;}
}
}
}
k++;
}
return k;
}
@wniemiec.executionflow.runtime.CollectCalls public static int countTotalArguments2(Object... args) {
int total = 0;
int i=0;
while (i<args.length) {int _fc2d2f3512fb90cb8b68513e3f7b9519=0;
int _51d057599880507ae9543a3ff61e868d=0;
int _ee7b50bbf9c6597e483a160e4e3f0544=0;
int _89a15adf8bede0ad69dc9804f6155e69=0;
if (i == 0) {
i++;
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {continue;}
}
}
}
}
total = i;
i++;
}
return total;
}
@wniemiec.executionflow.runtime.CollectCalls public int doWhileMethod(int a, int b) {
if (a == 0) {
return b;
}
if (b == 0) {
return a;
}
do {int _a55362712956e0696c0849209fd79dc0=0;
int _252e05c14b93be0d2d6578d9df9ac282=0;
int _16897b58e7c5345f8b6129b05e89e73c=0;
int _79a353aa20a5a5fc32927141d0a04390=0;
a++;
b--;
}
while (b != 0);
return a;
}
@wniemiec.executionflow.runtime.CollectCalls public int inlineWhile(int a) {
while (a > 0) {int _9454ebdfe9e81f472f17caeec837f35d=0;
int _a0250bbfd319ee1b3cdd62615210626e=0;
int _59ca0f25004fa2b853a790ff9f3095d4=0;
int _2db0d49b2f55520525e9c69a416db67f=0;
a--;
}
return a;
}
@wniemiec.executionflow.runtime.CollectCalls public int inlineDoWhile(int n)	{
if (n <= 0) {
n = 1;
}
do {int _e1130130357ef1e04ac441965c85ef9d=0;
int _c4686f7c1f8b2fbb5eee1fcefd75f3e6=0;
int _644d550bbcb563e526347690fa32cce=0;
int _2b5e06fc20bfd22f06b7f9fc04b49238=0;
n--;
}
while (n > 0);
return n;
}
@wniemiec.executionflow.runtime.CollectCalls public String ifElseMethod(int num) {
if (num < 0) {
return "Negative value";
}
String response = "";
if (num == 0) {
response = "Number zero";
}
else if (num == 1) {
response = "Number one";
}
else {int _b73a9543a5d35dee759de93e708084c8=0;
int _aa93c7dbed91493dc1c783c83f96a44d=0;
int _807d244cfe7e6c9cf164976b15b4f35=0;
int _f1cb173ed2c0b908b18e56547415c1b4=0;
response = "Number "+num;
}
return response;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean inlineIfElse(int n) {
if (n > 0) {
return true;
}
else {int _63e9b2a547472d84ffcab0fe58fdecfb=0;
int _a7d48f8b5d3bb602dc512bae5b0aa9c5=0;
int _55c835193f6a00405db64055ff738b9e=0;
int _8ecc0793721c4f4ee15a9fbea687eb80=0;
return false;
}
}
@wniemiec.executionflow.runtime.CollectCalls public int ifElseSameLine2(int num)	{
if (num > 0) {
num *= 0;
}
else {int _958038f5c50ca4de7d52b655c0775a2=0;
int _c5fac1e853134c090a15f39f4b99d8e5=0;
int _db47168d758cadcf2e14df2679f87891=0;
int _231dd46584089fba28f768706cab2ea5=0;
num = 10;
}
return num;
}
@wniemiec.executionflow.runtime.CollectCalls public String ifElseMethod2(int num) {
if (num < 0) {
return "Negative value";
}
String response = "";
if (num == 0) {
response = "Number zero";
}
else if (num == 1) {
response = "Number one";
}
else {int _ae1973375b519bf1c415bdce2d202ff8=0;
int _7cf584139ee7da421607ce1be7e1a975=0;
int _6a65c9d2e8c52e49c487f6bfcb746901=0;
int _dbb51d0a2ba8c60aa6cd90d4da316bdc=0;
response = "Number "+num;
}
return response;
}
@wniemiec.executionflow.runtime.CollectCalls public void m1(int num){
someMethod();
}
@wniemiec.executionflow.runtime.CollectCalls private void m2(int num){
someMethod();
}
@wniemiec.executionflow.runtime.CollectCalls public static void m3(int num){
someMethod();
}
@wniemiec.executionflow.runtime.CollectCalls public synchronized void m4(int num){
someMethod();
}
@wniemiec.executionflow.runtime.CollectCalls public char switchCaseMethod(char letter) {
letter = Character.toUpperCase(letter);
switch (letter) {
case 'Á':int _23f2e6fd6e65c38e25fb7a919ced5817=0;
int _f99d6c351f11889cc3cf6463bb878e51=0;
int _4148c0bee3baad546152025d45893591=0;
int _fe1057916f8a660da74d53cd9f202425=0;
case 'À':int _5f822954c07d33eb5d3102c5a80c2c80=0;
int _55e1febd94b6d15a359fd6a732439617=0;
int _d3ffcf114bc5f8e18f54270d405b0a21=0;
int _37f99038a3364e4ea29ec974666f5d3c=0;
case 'Ã':int _7eeb1cd9aa4fed470b2fb57c2e890aef=0;
int _c118ade5e12c00c20f39ffbe251f0b01=0;
int _6da1db342866f5f86b6de1cd04d26dc8=0;
int _7f272de130378896e5ad6a9228135346=0;
case 'Â':int _68d486b19e9158d771398850538b0d32=0;
int _36705f85bf883779ae846a46d0ecac8d=0;
int _326f29138a5252ff5125a7a8b8fd12c0=0;
int _6b41751037960e33c2578f95e8fdfb56=0;
letter = 'A';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'É':int _d25c7019dc972d829831f9f7961f97bb=0;
int _3078f224f91196d366d4a46a7176dd42=0;
int _677574ec81a702e4555e57dbc210fa95=0;
int _e7ee01669c0c4f13855e3be94b50d169=0;
case 'È':int _66146f4c36514c3b196b66ca99550496=0;
int _18207a4fdb04fa015aedf7b362382527=0;
int _7b05d3a55a348d600267def61b961700=0;
int _9e0785c8fd771b1c49dc8113caae2c04=0;
case 'Ê':int _154ce57d706078c68a3d756a281236d5=0;
int _4e224020fac668be73bc38691b96e6dd=0;
int _68e8f9069fe7b171f82fcc9a47c1d77c=0;
int _ea46c72bc8925d11e1c25f86ce68a67=0;
letter = 'E';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ì':int _2f25488387995b336142103c651d4cb=0;
int _146dc562c1e896e102bf16f463f4dd97=0;
int _c9cfcd61c3b7eae80839713a39aa91ff=0;
int _6efad417a2c9f880627df8103bd25c3c=0;
case 'Í':int _fdc028d67a9d1c292c645218bdd3ced6=0;
int _f81bd91dd0c8cddfe7e1178c4bf22ec8=0;
int _fd7985ea35f5e2246e8cc7b47497f778=0;
int _e93c3eb2a698f1d3c3e6a71133cbe2cd=0;
case 'Î':int _143abae929704203b9b89ae448958fc9=0;
int _af6c55c5d62e3e4e7565f9d8bdfc455e=0;
int _1163559c3c3fd6271c5e4b8a0556fda7=0;
int _f59f0bde78b9b8513fdc3abb2bc17087=0;
letter = 'I';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ò':int _5086f139808765438dfa2babf2b1a72=0;
int _75e724c5f4fa9afcf2518ed32563e21d=0;
int _8842191933f170b8d2e762cccc505546=0;
int _4dacbaa7a54ae1a6689cef7cfbb0e87c=0;
case 'Ó':int _fdb286f1a5a26c7a00acaee5139a8399=0;
int _22aaa1c55c6bf49204369a8753f9553c=0;
int _a9a3c7ee193fd2a81288ca710337d159=0;
int _d62c651d7d2dd31e6e45ee1afe154636=0;
case 'Ô':int _c4d1d4e4bf322161aa5c406e9efa578b=0;
int _152de55733dcffb3612db7fa2adb5b74=0;
int _a95793592857ce0b977a8044553c61d4=0;
int _c0c0345523c3c11ee305e104344093e3=0;
case 'Õ':int _b638a5cc2cf9b78c02460bfb316f45=0;
int _8b049694bef9ef35b6a7dd3177004f59=0;
int _bacbe86472dd758761ddf798f096367e=0;
int _5477a746b5088aac324fef4225b9debd=0;
letter = 'O';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ú':int _799e7e8d927bacb8f7948c5e88d71efe=0;
int _37a9cbea58c235752bae776a37338178=0;
int _c1670e65edb40467717316fa7952374c=0;
int _a3b633c282e8a53fd59578ca6e9fb6cf=0;
case 'Ù':int _a7bf1d9625667cb4b6431d4719e52448=0;
int _1202d7cc3bc6773b738803ff65daf4d5=0;
int _23f043e7fa9884a06fbcec913bed696b=0;
int _107b5783c055903cb8f5952b5f85cfde=0;
case 'Û':int _8cd5786f758b6068d07e8be5e8d6fcb1=0;
int _4eca1ba5b5b834bc12c63c8373f5e5d3=0;
int _4e860d614cbf56bcde02ff5e20bdd9ca=0;
int _13cf03ed5386b85cf2c3694adabb4677=0;
letter = 'U';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
return letter;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean tryCatchMethod_try() {
File f = new File("tmp");
FileWriter fw;int _3c039cd231d8f386d9402f4830815160=0;
int _95c91a914fb1f776a34af9342b822f97=0;
int _393c1ec5275bf4801ace02475f61404d=0;
int _a0e39706f21c90ffd66e29ad2d1f76e7=0;
try {int _e8e3c1fb9fc7ede2a66e3ec651710561=0;
int _151519b3ab3023478a11ab2c8ae94437=0;
int _981bd7e91e80e099018c467a5cac2599=0;
int _9d702dcaa61cb0d244af0fb9e46218af=0;
fw = new FileWriter(f);
fw.write('x');
fw.close();
f.delete();
}
catch (IOException e) {
return false;
}
return true;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean tryCatchMethod_catch() {
File f = new File("tmp");
FileWriter fw;int _1dbc2383463bde2a1729bba4388703f0=0;
int _ca4b3ef9671acb513f96becf035b073=0;
int _3bb72a9d8b0b492f0ee5ded211530219=0;
int _c68096253c705304ce301a22900cf706=0;
try {int _9d682d467f1a9e1f57a8d6133de2e120=0;
int _6f7521b1065a66fbfeb3a442c556f1db=0;
int _6d1d838fd7d98486bee2a2e610c6f43e=0;
int _feed02bb94db865ab16c0270849378dc=0;
throw new IOException();
}
catch (IOException e) {
return false;
}
}
@wniemiec.executionflow.runtime.CollectCalls public static void test3(int x, int y, String k) {
x = 0;
while (x < 3) {int _d6b22b88443cccca57a99d83a36654e7=0;
int _59afd7ddcaf5985f43a9bd4b5392849a=0;
int _d7e8ff357588129546516c10b7a5013e=0;
int _3aeb880f516b42aa464f342eae8ad4d2=0;
x++;
}
x = 3;
}
@wniemiec.executionflow.runtime.CollectCalls private static void someMethod() {
}
}
