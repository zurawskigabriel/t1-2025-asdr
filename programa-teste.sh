echo 'Iniciando programa de teste...'

make clean;
make;

echo 'Executando ASDR com um caso de teste valido:\n'

java AdsrSample.java teste06.txt;

echo '\nCaso de teste valido concluido!\n'

echo '\nExecutando ASDR com um caso de teste invalido:\n'

java AsdrSample.java erro06.txt;

echo '\nCaso de teste invalido concluido!\n'
