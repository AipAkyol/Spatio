## Overview



## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Rules for Interrupts (in Turkish)

1-Başlangıç Yerleşimi: Tüm nöronlar kare mazgalların köşeleri üzerine rastgele yerleştirilir. 
2-Nöronun şekli dairedir. Ve her nöronun çapı 0,5 cm dir
3-İki tip nöron vardır: eksitatör ve inhibitör. 
4-Ekstitatör nöron kırmızı daire ile gösterilir.
5-İnhibitör nöron mavi daire ile gösterilir.
6-Eksitatör nöronlar sinyalleri güçlendirir.
7-İnhibitör nöronlar sinyalleri zayıflatır.
8-Her nöron en az 1, en fazla 4 bağ oluşturabilir. 
9-İnhibitör nöronlar arasında bağ kurulamaz.
10-Bağ 2 nöron dairesinin merkezini birbirine bağlayan bir çizgi ile gösterilir. Bağ ilk oluştuğunda bağ çizgisi 0,05 cm kalınlığındadır. Bağ  derecesi 1 artar ise çizgi kalınlığı 0,01 cm artar. Bağ  derecesi 1 azalır ise çizgi kalınlığı 0,01 cm azalır.
11-Her bağın bir başlangıç gücü vardır.
12-Bir nöron, bağlantılı diğer bir nöronun ateşlenmesinden hemen sonraki zaman adımında ateşlenirse aralarındaki bağ güçlenir.
13-Her bağ 4 derece gücünde oluşur.
14-Bağlar zayıfladığında ve 2 derece gücünün bir altına düştüğünde kopar.
15-Bir nöron, aldığı elektriksel girdiyi, bağ derecesi orantısında bağlı olduğu diğer nöronlara iletir. Örneğin, eksitatör bir nörona 9 mV'luk bir elektriksel uyarı geldiğinde ilk önce bu elektrik ekstitatör olduğu için 1 derece yükselerek 10 mV olur, bu nöronun sinaptik ağırlıkları sırasıyla 3, 5 ve 2 olan üç farklı nörona bağlandığı düşünülürse; uyarı, sırasıyla 3 mV, 5 mV ve 2 mV olarak bu nöronlara dağıtılacaktır. Bu durum, nöronun ateşleme eşiğini aşması durumunda gerçekleşir.
16-Her nöronun ateşleme eşik değeri vardır ve bu ilk başta 3 mV dir. 1 mV’nin altına düşemez.
17-Eksitatör nöronlar ateşlendiğinde kendilerine ulaşan elektriği 1mV arttırarak diğer nöronlara iletir. 
18-İnhibitör nöronlar ateşlendiğinde kendilerine ulaşan elektriği 1mV azaltarak diğer nöronlara iletir.
19-İnhibitör nöronlar, eksitatör nöronların ateşlenmesini engellemede başarılı olursa inhibitörden ekstitatöre olan bağlar 1 derece güçlenir, aksi takdirde 1 derece zayıflar.
20-Her nöronun durumu (aktif = 1, inaktif = 0) aldığı toplam uyarıcı ve engelleyici girdilere bağlıdır.
21-Nöron aktif ise ateşler ve elektriği iletir değil ise kendine gelen enerji 0’lanır. 
22-Eksitatör ve inhibitör girdiler ayrı ayrı hesaplanır ve nöronun ateşleyip ateşlemeyeceğine karar verilir.
23-Nöron ateşlendikten sonraki 5 birim zaman içinde tekrar ateşliyorsa eşik değeri 1 mV artar.
24-Nöron ateşlendikten sonraki 5 birim zaman içinde tekrar ateşliyorsa eşik değeri 1 mV düşer.
25-Sistem her 5 zaman biriminde bir 10 mV olacak şekilde en sol iki nörona dışarıdan verilir. Her ikiside 10 mV alır.
26-%10 olasılıkla, birbirine yakın nöronlar arasında yeni bağlar oluşabilir. Bağ ilk oluştuğunda bağ çizgisi 0,05 cm kalınlığındadır.