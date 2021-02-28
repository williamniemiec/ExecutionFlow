package wniemiec.executionflow.io.processing.manager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class fileprocessing {
@wniemiec.executionflow.runtime.CollectCalls public static int countTotalArguments(Object... args) {
int i = 2;
int k;int _66368451c19b1874fbe57276de6b4b60=0;
int _cb3e63568e45c338de0740509d9d6e69=0;
int _54f78c8d02319f5013e67ddaaa47a3cd=0;
int _86e1cd2af2b3f1ef781a9194c26dddc8=0;
k=0;
while (k<args.length) {int _c1e337597d72b31f0760fdc45101b531=0;
int _c92a0bca7eb596712b4f9596b42cbd93=0;
int _54f7056a5d223f6016c05b47345a4c45=0;
int _ec5731c66d189acd1e8c14ea67b9c76c=0;
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
while (i<args.length) {int _1c9ea29c9696932495d642a64f549692=0;
int _d9c22799bd110f463c2ef92c24b9f8eb=0;
int _90f2bcda83e2f78c51e8bb3fa88819ce=0;
int _f43629cf580dd335e69083b3e156260a=0;
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
do {int _64620b5f097f92156d18a95246077f10=0;
int _b047cd65901dd81d81cb46eff1d4cfbe=0;
int _d9f6e3aad324a0a52bcac789763c4b1=0;
int _98d5fd9bb62efb98abdc5df3499759ba=0;
a++;
b--;
}
while (b != 0);
return a;
}
@wniemiec.executionflow.runtime.CollectCalls public int inlineWhile(int a) {
while (a > 0) {int _ad0543632fe6530cf955de3ea8c16d2c=0;
int _5983bc99bcd92630c4c8d8fa3452e70f=0;
int _11a5165bc5757404b1eb43e48a003f60=0;
int _90e0ed6a0761b0b2b3d4fe613e1fe573=0;
a--;
}
return a;
}
@wniemiec.executionflow.runtime.CollectCalls public int inlineDoWhile(int n)	{
if (n <= 0) {
n = 1;
}
do {int _598513b63bdc8532aea85f9fba949d25=0;
int _879e2287846419f7c2140703c68b6921=0;
int _1bc56bdbd6ac28d7682908b3f41ec4c=0;
int _36d7af37e7e559731a736c2d98bde27f=0;
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
else {int _c942ee12decad333576ba7be41a2629d=0;
int _dce04f7c554c0e254f3e939a1d6177a=0;
int _1bfc210a5afe4528f1d1e969cde63d82=0;
int _9e81f26678723e6873847390596560bf=0;
response = "Number "+num;
}
return response;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean inlineIfElse(int n) {
if (n > 0) {
return true;
}
else {int _4b5d1c3db447aa92092d6274cfb09090=0;
int _9b2a4fe6eb635691930171afda4e5b49=0;
int _31ae7b03646d4c2366ff9dd97ad1fe16=0;
int _82e35716423187079a3cb9929e7f95c4=0;
return false;
}
}
@wniemiec.executionflow.runtime.CollectCalls public int ifElseSameLine2(int num)	{
if (num > 0) {
num *= 0;
}
else {int _359340cdb218b4efdd3c63423cfb46c1=0;
int _91dccd762fe33208afe9272f13e4223a=0;
int _6a27b39596f401d9cd13d99318f1339a=0;
int _f89c2adf346755c62f0e973daffe2618=0;
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
else {int _f38aed19ecdb29c7861020f94b622cd7=0;
int _a013f4f1dafc4f6a4b98c39907159a03=0;
int _40331e906102ead650848c2e45a25a17=0;
int _18a3d657ba2ed8ae28475db25ceedb01=0;
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
case 'Á':int _cc309b314ebd07f8710f0f706b074402=0;
int _d874ad31d268f0543f70c0178537bc99=0;
int _e48da8c9d79a0051ea62a8ad2c78f57b=0;
int _c4131d8b6971a1ae85f0b0ea50d0f505=0;
case 'À':int _40be4c42335fa8f83f4b7fca039cdbc=0;
int _7e2836a8cc30393c6a53cec643d6936c=0;
int _ee58914578df08e11edf50831abc67ca=0;
int _f7225a31d3917e35773df18f73d86b71=0;
case 'Ã':int _7f990493d26b663945d84d87c5425adf=0;
int _e83ea78dbc6f23b23f46a82f2172f1dd=0;
int _32376825cbb2b4d2b641c750fd6d0d4d=0;
int _e103f7d1a75a585758d19eeb1b805ce1=0;
case 'Â':int _4772a4be9704dadb7efd7f648e2d8bd8=0;
int _18ba5419d31945bebe6014a1e1a9bd80=0;
int _4517e1e8ae6459ac8157c1369ce4e0b6=0;
int _cffc48a9c1995eaa44dea7daacdc9d1d=0;
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
case 'É':int _bd5ae5703b5c060423cbbca89abc80d7=0;
int _8983adf913b178487cfc61fc834f3f9a=0;
int _7aea508f13804d4e1a5223e6cbbd7d0c=0;
int _8eff8fcb3a0713fa7a0af6bbcea93571=0;
case 'È':int _ecd5b59429cdd9c9f0ff0e1b52f14c3e=0;
int _2e23538bacf3de2fc613ba47e1b3f5b=0;
int _2345a869999b28a0f40a71d35c8af05=0;
int _f77aac9f46072981a8782d3fba41ce37=0;
case 'Ê':int _a0cd858c185a677d10c00ba9a7a40941=0;
int _9a5eace9b82db95216b65af2edd62357=0;
int _624c0dcb7cb39b3ca0f8ab07fe537774=0;
int _710428f8e136601b30379c183057b390=0;
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
case 'Ì':int _f83106dd216fc3a3e254b82974064d71=0;
int _f319868588b1b3d11840742c3468a936=0;
int _86f2c49bb64ca898249c92e1302f4395=0;
int _6114ff46b4211cf92808306d09a011df=0;
case 'Í':int _5c8e5900635995f0f107b00adaef6e46=0;
int _6b434c024b7f5ba28f06a995fdf8eec2=0;
int _b5231b1c367893ea986cb917a3e98332=0;
int _168c0b5476dad7f2526026b9c44d2f39=0;
case 'Î':int _bf08b4ca2544eea01d221807c8ded73e=0;
int _88a0efffb781e5b8332209a71955861b=0;
int _109f20f909a95e629bd703f22e205968=0;
int _54566d46012db97441558fdfc39139ca=0;
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
case 'Ò':int _359c02e46ca50e2e26210a52ae993209=0;
int _4ad4df076ab55c7868a531edebdadde2=0;
int _44d246191c7d0f1df9aa2b42e5f7c223=0;
int _b49438af68b83f3377b053e6a0d0b7a2=0;
case 'Ó':int _7af515c7d668f543195b7a7a488567f2=0;
int _b790d0910d6d51b30154d91efcd3fce=0;
int _dabd899395144b2579128832d3acbc37=0;
int _a003f2a0eb045cf6e6a68a63c3be29c1=0;
case 'Ô':int _1e90f9c5a25d2d250d882bc394d76cb6=0;
int _374c2f2093e1cb7312c2bffa5302d76=0;
int _3ba7946961454e8560e6e77616a34266=0;
int _cea85a9cd2748cb054043f1032cfef23=0;
case 'Õ':int _5a1e0ac5cc50f0de8d3788c5adfe4410=0;
int _d66340ab9da5c3b9c8a1dd0d5e836995=0;
int _4dc61439d6b7608c22961cae9b9ada3a=0;
int _4c6ddbd76e6626b1a6da1df953db7aa9=0;
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
case 'Ú':int _acabfa8accf6e7947b75d34cc9855ed4=0;
int _eeabb542e2bd90714e58fc8bdac80dc1=0;
int _284402f98aa68b78dd13a2b8af9c4738=0;
int _d2f9177b524cdfca5c1f04dd5389ee1e=0;
case 'Ù':int _58a5af75f59ce56e05cae6e3d72bd2fb=0;
int _6872c2cd36755cb747343cdb6e8c99ca=0;
int _690e0009b83db5f8787c9c24e74edf7b=0;
int _6156f377390e798c5cdaa28bf4778f60=0;
case 'Û':int _3daabc51034f2caadf92bbd60e428cd9=0;
int _1c1df616b19dba65d6219344b7eab07=0;
int _34ab06c8bf4d3422fabeb899f2ff0d21=0;
int _909803ef4c692211ee838488c160c385=0;
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
FileWriter fw;int _de12c083bdfd836c49508ae344002043=0;
int _11da9b39a59aac67c9aae36eac2b8edb=0;
int _6db6bcee5d0bb56d635c15cb3a84eff=0;
int _e16b8ba7a6ad390c4d452eb9ebfeab89=0;
try {int _3af4a6971a7952aa8646a70b2c3795ba=0;
int _95c47829c19bf85184c81ff5d1accd58=0;
int _2aef260d7e2596b4311d6252f988165c=0;
int _6ba4ff8dc5953436380aaaf23cec7eff=0;
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
FileWriter fw;int _6991fc4e83617543a35ffbe3a953be75=0;
int _bf8c2ea5bc5c212b572e6a4126753bb6=0;
int _9ba2aba08ba07992e24bd5e88414bde7=0;
int _1053c4fe0751146e64ba36c7ffc7102f=0;
try {int _5d5e35f98cca3914ea38806a56f5cccc=0;
int _445e373ee83f06a92260c7d2c6365a44=0;
int _7971a512b93de5e7dd24e68b0df7292c=0;
int _cd41840a97f16f0f8e4c4bf476e2c943=0;
throw new IOException();
}
catch (IOException e) {
return false;
}
}
@wniemiec.executionflow.runtime.CollectCalls public static void test3(int x, int y, String k) {
x = 0;
while (x < 3) {int _a769c1c329299546452dfa655cc7cbf1=0;
int _6eddff6f93d834519be4619de9eb7ab1=0;
int _d24d000a16c273ae0abb90989fa838d4=0;
int _9f1e8d5777e824c1994bab0479e9c04=0;
x++;
}
x = 3;
}
@wniemiec.executionflow.runtime.CollectCalls public static void someMethod() {
}
}
