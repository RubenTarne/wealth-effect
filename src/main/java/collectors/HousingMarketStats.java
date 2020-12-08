package collectors;

import housing.*;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Arrays;

/**************************************************************************************************
 * Class to collect regional sale market statistics
 *
 * @author daniel, Adrian Carro
 *
 *************************************************************************************************/
public class HousingMarketStats {

	//------------------//
	//----- Fields -----//
	//------------------//

	// General fields
	private HousingMarket           market; // Declared HousingMarket so that it can accommodate both sale and rental markets
	private Config                  config = Model.config; // Passes the Model's configuration parameters object to a private field

	// Variables computed at initialisation
	double []                       referencePricePerQuality;

	// Variables computed before market clearing
	private int                     nBuyers;
    private int                     nBTLBuyers;
    private int						nFTBBuyers;
	private int                     nSellers;
    private int                     nNewSellers;
    private int                     nBTLSellers;
	private double                  sumBidPrices;
	private double                  sumOfferPrices;
	private double []               offerPrices;
	private double []               bidPrices;

	// Variables computed during market clearing, counters
	private int                     salesCount; // Dummy variable to count sales
	private int                     ftbSalesCount; // Dummy variable to count sales to first-time buyers
	private int                     btlSalesCount; // Dummy variable to count sales to buy-to-let investors
	private double                  sumSoldReferencePriceCount; // Dummy counter
	private double                  sumSoldPriceCount; // Dummy counter
	private double                  sumMonthsOnMarketCount; // Dummy counter
    private double []               sumMonthsOnMarketPerQualityCount; // Dummy counter
	private double []               sumSalePricePerQualityCount; // Dummy counter
	private int []                  nSalesPerQualityCount; // Dummy counter
	private double 					moneyToConstructionSectorCount; // Dummy counter

	// Variables computed after market clearing to keep the previous values during the clearing
	private int                     nSales; // Number of sales
	private int	                    nFTBSales; // Number of sales to first-time buyers
	private int	                    nBTLSales; // Number of sales to buy-to-let investors
	private int 	                nUnsoldNewBuild; // Accumulated number of new built properties still unsold after market clearing
	private double                  sumSoldReferencePrice; // Sum of reference prices for the qualities of properties sold this month
	private double                  sumSoldPrice; // Sum of prices of properties sold this month
	private double                  sumMonthsOnMarket; // Sum of the number of months on the market for properties sold this month
	private double []               sumSalePricePerQuality; // Sum of the price for each quality band for properties sold this month
	private int []                  nSalesPerQuality; // Number of sales for each quality band for properties sold this month
	private double					averageHouseSaleQuality; // average quality band of houses sold 
	private double 					moneyToConstructionSector; // money flowing out of the simulation via the construction sector
	
	// Other variables computed after market clearing
	private double                  expAvMonthsOnMarket; // Exponential moving average of the number of months on the market
    private double []               sumMonthsOnMarketPerQuality; // Sum of the months on market for each quality band for properties sold this month
    private double []               expAvMonthsOnMarketPerQuality; // Exponential moving average of the months on market for each quality band
    private double                  expAvSalePrice; // Exponential moving average of sale prices
	private double []               expAvSalePricePerQuality; // Exponential moving average of the price for each quality band
	private double                  housePriceIndex;
	private DescriptiveStatistics   HPIRecord;
	private double                  annualHousePriceAppreciation;
	private double                  longTermHousePriceAppreciation;
	
	private double []				arrayHPI = {0, 0.9811290447354776, 0.9936821555741089, 0.976482085662997, 0.8130980622542793, 0.8504612363402271, 0.8638540241462886, 0.8567688821422197, 0.7291064336480247, 0.7893994540338344, 0.6960789958078977, 0.7272681457941275, 0.7367386106681588, 0.5911060386057185, 0.6149311368043595, 0.5985500890516462, 0.5372234995030769, 0.5602877826176, 0.5553954631919781, 0.4681719000776716, 0.5012570338690239, 0.4984056097086188, 0.524495910053383, 0.5323034840529747, 0.5119610855748519, 0.5186117317928146, 0.5208795525316771, 0.5004584368355462, 0.5016227709384663, 0.4836422456732672, 0.4703411110459094, 0.46031412452534815, 0.4729128056667535, 0.46673303538912464, 0.44813798101313546, 0.46422384150914875, 0.4530595759837259, 0.4561859127600624, 0.4506416726342056, 0.4227929094787916, 0.4355762974507487, 0.419286734520085, 0.4189002184095657, 0.414420107833771, 0.41732875832682803, 0.42209448986114756, 0.42698160599849416, 0.4250226757362186, 0.4286445920124245, 0.42260631637142515, 0.42455103041765496, 0.4259484069857665, 0.43025759524329454, 0.4293704999076426, 0.43652725228778894, 0.4463476153456657, 0.45754943864893843, 0.4646959070267232, 0.4855990405415536, 0.5337892568645122, 0.578741292611828, 0.6473719781163312, 0.7575362677823497, 0.9294562771580752, 1.0847608930658388, 1.1623108384251444, 1.215596209235387, 1.237130215145056, 1.2004286922533152, 1.2191939032768049, 1.2508386163256313, 1.255390660640456, 1.2869278863231752, 1.318224120314631, 1.3585731080526817, 1.3770814299411305, 1.4091557863004232, 1.4055422863711158, 1.3936748731339166, 1.4379214370377087, 1.4135313047491107, 1.4106017914866567, 1.4005978542041835, 1.314752253660613, 1.3162605542671062, 1.255367372713746, 1.2727266149011387, 1.2827735708227719, 1.304671037342851, 1.3013625946787366, 1.3066649231971563, 1.3240007211543487, 1.3375236408213904, 1.306833542912258, 1.300921591656526, 1.3052879872450944, 1.2923925917642927, 1.2823202953762098, 1.2955146804609927, 1.2637088546132056, 1.1597647115270662, 1.217475761804042, 1.2180685746805304, 1.095806867208105, 1.0596430205291287, 1.0354231019232436, 1.0035688856092995, 1.0255856681485465, 1.0213678811660623, 0.9062298788411375, 0.819747628371266, 0.8806452099521641, 0.8950176276803726, 0.8994927121141361, 0.8683594705981299, 0.8281136207788762, 0.7874107719077639, 0.7070387702296199, 0.7379191840608431, 0.7994438749067644, 0.759538848839772, 0.7387954583278655, 0.7472579074362515, 0.6536676007433958, 0.6672735439311239, 0.6450091143729784, 0.6211740206392768, 0.606178361720402, 0.6025264781242302, 0.612335646308, 0.5907347710328974, 0.5856090612745294, 0.6049818722941993, 0.6067560254820935, 0.5911672297893122, 0.5928307211106979, 0.5189400755931148, 0.5066410253846663, 0.5210356440572107, 0.5057471260504836, 0.4989387373223534, 0.4691850023576976, 0.47384862730737487, 0.45193436293363715, 0.4555828107067661, 0.4661057286548766, 0.46153198475849816, 0.4442045406898127, 0.44918605343514195, 0.45743743452492514, 0.44305444197638905, 0.4496387788098664, 0.44547628278351487, 0.4448646615124006, 0.456647047560722, 0.44638098014230404, 0.43657331618966494, 0.44161629265720653, 0.43987210526227655, 0.439225860632572, 0.42596661635355443, 0.3988609038342809, 0.41063273455216587, 0.4175362033320497, 0.4195435249467085, 0.42586151815099205, 0.42523025026500266, 0.43262819252531165, 0.4466256749491146, 0.46216135955129634, 0.5067511474675975, 0.5268411620396957, 0.5739101586008503, 0.6300554623302419, 0.6967762839275212, 0.8046861634069533, 0.9840070464528715, 1.1142049679222472, 1.1997339085048047, 1.228874642865734, 1.2739065131973188, 1.3113263147256873, 1.3531887561961708, 1.3676703674157853, 1.38650024968144, 1.3948269945227085, 1.3980040835494834, 1.415647727114388, 1.4252570596255478, 1.459111473456939, 1.4796807083453523, 1.5051899577760501, 1.4965880764067123, 1.465564335611846, 1.4856591825422445, 1.5146417695908432, 1.5349931168601856, 1.5828411669116036, 1.51523399890471, 1.4921569824237182, 1.5151281204646092, 1.4658203536356493, 1.4360678239079805, 1.4231126248545138, 1.472211911886948, 1.4846402528225575, 1.4449214472705778, 1.475911171687247, 1.374512391048611, 1.3827595466499991, 1.3549348929649472, 1.1712001458432355, 1.2209229561360253, 1.2024250710580906, 1.0606182850287786, 1.0273528033226, 1.1103216399286975, 1.092748308020296, 1.136741947171134, 1.064806639082599, 1.008122311339842, 0.954403696339231, 0.7755406259670817, 0.8194585030573498, 0.8325339518470061, 0.7420704794001679, 0.7305948081602698, 0.6751764682580815, 0.6301634219950866, 0.6397335544888519, 0.6569749269443764, 0.6472306421157678, 0.6385510129496098, 0.5645240998040736, 0.5876901389681731, 0.5831498523080371, 0.5072142508960825, 0.5171131931084926, 0.4767722494915451, 0.5058452263070646, 0.4874265718275877, 0.4674346472721386, 0.474701840167388, 0.46662797807860473, 0.4733669049887832, 0.4515053946188111, 0.4525872248604889, 0.4511825704086103, 0.4463516843576194, 0.4434392738668975, 0.43820863507597935, 0.4344990652845258, 0.43439466817099637, 0.4385127886906027, 0.43248390658700864, 0.43062636916851105, 0.4226767141173084, 0.4259435256171719, 0.41793199664032593, 0.420558919178409, 0.41728001460707287, 0.42102606467838294, 0.4168473294064316, 0.42787680351565544, 0.4241740382371388, 0.4278509122840111, 0.42559540204886664, 0.4387130254553157, 0.4422348196414618, 0.4426327690437777, 0.4495767998189252, 0.45925366966816805, 0.4781158127326117, 0.5177621742448251, 0.559926740319517, 0.640838153564283, 0.75531420747158, 0.9754368872192177, 1.1329959165742216, 1.2177501062104048, 1.2666122746799529, 1.2862419829789342, 1.3324768485676877, 1.4054623705852045, 1.3942881554371953, 1.4026767262190676, 1.3700869013172092, 1.397045124176863, 1.44461547324143, 1.446730663990826, 1.4415224476015815, 1.4749292682833088, 1.3897092237314606, 1.3776540947749547, 1.365299274352948, 1.3489734253730352, 1.3773469691510625, 1.34937168493695, 1.359700469715349, 1.3749020266555982, 1.3758903203915434, 1.3664065473640923, 1.3692963045088924, 1.3902893557343983, 1.3460035496696285, 1.3618003213283039, 1.4077187011227208, 1.3164219875126852, 1.347286077961115, 1.301394702000269, 1.1790174422735447, 1.1750956675475812, 1.0331309973600016, 1.009971739370898, 1.0351333074326439, 1.0631880854620508, 0.9732760472634241, 1.0679366009192768, 0.9806853787802251, 1.0190977935493128, 1.0226675767548519, 0.9839990794370054, 0.9991765850174608, 0.9688031572564791, 0.9476086575615884, 0.8694233202182524, 0.7713752066068289, 0.7628983828388854, 0.7923059691714391, 0.7883817705296846, 0.8001354791543024, 0.7609867579411529, 0.6673293187936257, 0.6805807581633486, 0.6122702836418995, 0.6118126666232003, 0.6231347741488311, 0.6093823054385406, 0.6329383344324904, 0.6129233322261279, 0.6117500649253604, 0.582187780445379, 0.5748391967873999, 0.5890756412700924, 0.5503532336595583, 0.49750707725236193, 0.5282143935291728, 0.5106164462710289, 0.5173493249591051, 0.5148785471982661, 0.5247375595413929, 0.48230698174477754, 0.475615540105432, 0.47597648375827245, 0.4866224752158696, 0.48412047238466943, 0.4607071415375316, 0.4781359951194359, 0.46587116959592134, 0.4634011334363011, 0.4561942665793915, 0.4629697499492279, 0.46010316420969305, 0.4478844070684566, 0.4561673814444617, 0.45539722270494076, 0.4441800481420553, 0.44287888370786094, 0.43864922056241207, 0.4427365270562363, 0.45151329557360603, 0.44678293244192013, 0.4522494154938712, 0.45522850844850055, 0.45661241802025754, 0.4500447623585106, 0.45275544035106285, 0.45514931590525515, 0.46072019626353594, 0.46218746627943474, 0.4615524096985865, 0.4774090883118892, 0.49058787184693026, 0.4977706398756781, 0.5329317480049898, 0.5768820244755269, 0.6083311045476198, 0.6943385673478535, 0.8005485425111428, 0.9412352903957848, 1.0616009668193607, 1.1536223171775672, 1.168103603027597, 1.1960813880868184, 1.281778124510205, 1.3351646412014764, 1.3603762948558151, 1.3331354227779832, 1.3745733398241393, 1.388721140549104, 1.350182686378563, 1.401376773737821, 1.4138663376348652, 1.4315242288007906, 1.3251116276060944, 1.3769996603415546, 1.3845221127820106, 1.4089521410985841, 1.407191444209351, 1.368191919236927, 1.4314772154616733, 1.434878468560226, 1.4108604187176428, 1.3908552225526254, 1.4203934020124491, 1.4072561605072078, 1.3734416309146205, 1.3246644857913947, 1.3622742669795944, 1.3609720760516049, 1.2241711637333552, 1.2113912734624026, 1.216674040055969, 1.1508656892956035, 1.1391713419165255, 1.1930605471543028, 1.0944614390400382, 1.157926188942845, 1.0884076467101316, 1.1144970073657847, 1.0918053895261315, 1.007730038388678, 0.8464385281821961, 0.710418916990539, 0.7693143975167472, 0.7885595352013695, 0.7908853448595772, 0.6657623278548371, 0.7072173559148222, 0.7093607880028429, 0.7144739992517183, 0.6797363204515536, 0.690909411506996, 0.6436422683198293, 0.6590419326219993, 0.66743786560158, 0.6505016050304869, 0.6268584422386864, 0.7279508739377392, 0.6416429498819717, 0.6485921303800252, 0.6731387985591402, 0.7157832746736353, 0.5999809458952706, 0.6345929030173322, 0.6158285595348295, 0.6253755943437811, 0.5636873035691198, 0.5793945759262686, 0.5832119828517985, 0.5676982533324918, 0.5569476633606069, 0.5463227244622184, 0.5566132138609059, 0.5592969968153338, 0.532083095061257, 0.5324799883585242, 0.5527104310433104, 0.5488742556414552, 0.5366450900596541, 0.5320738296272192, 0.5290808903426176, 0.5221281231065747, 0.515206252141697, 0.5184531474924382, 0.4894606564451519, 0.4770078027394085, 0.4916049572613495, 0.4669975750624061, 0.46614878841643564, 0.4535731068413492, 0.4543691426519478, 0.45758641612258255, 0.45268867314732475, 0.4535790603482304, 0.454298225365314, 0.4556851482835629, 0.4615288554205184, 0.44902640560687557, 0.4509322840557568, 0.45304828946474934, 0.45325940894480427, 0.4510856488855937, 0.45590999940507404, 0.4382102135091056, 0.45041258048921057, 0.4358157640272406, 0.44363637465182687, 0.43795814995268506, 0.45725653419072665, 0.45800185805814236, 0.4550315633838054, 0.45583376334183545, 0.4605114505122017, 0.4691973450763031, 0.47725482997041924, 0.4912967769718492, 0.5109325481771548, 0.5441877144923397, 0.5903731838208338, 0.6574702687275045, 0.7066005440477456, 0.8793029560858892, 1.0684077643617849, 1.2052268750228652, 1.208118297359349, 1.237863226826462, 1.2591599485524698, 1.3058464686855689, 1.32898832603215, 1.3649487550985124, 1.3784158481146118, 1.3833808837739592, 1.441972993652892, 1.518022543242853, 1.5053717419881854, 1.4425857299820535, 1.4416704878127777, 1.5059257613245156, 1.564911214815875, 1.5405111360659043, 1.562083715223683, 1.592433176120567, 1.5907275642125611, 1.564358584384355, 1.667033606963441, 1.4447790560628564, 1.533241538487616, 1.492356903677814, 1.5163740637320533, 1.4478872452437208, 1.411754459415259, 1.4301786380655181, 1.4058387198615339, 1.3323090919037726, 1.3519247397063845, 1.3255859798211143, 1.3394564759329137, 1.345732182306476, 1.2950300865796527, 1.2684051743280087, 1.190283851973223, 1.1660869496933692, 1.2064705429785176, 1.1872049618823242, 1.2049582696654204, 1.0946692167970251, 1.1599548887929267, 1.1286525194907688, 0.9815530141673589, 1.0828260931117217, 0.8872892959063108, 0.949455227121431, 1.0014223461544363, 0.8830973130388121, 0.8835318737412726, 0.8068594436628588, 0.8354238997905472, 0.7092054172800543, 0.695305353051682, 0.6742677645306135, 0.6384768488597378, 0.7031851227832805, 0.6337448690135875, 0.6175504126582352, 0.6463512417742797, 0.6101014006722737, 0.6503108675951533, 0.6108547521282834, 0.5969098369592051, 0.6021988012948375, 0.5575698202677534, 0.5123138007033949, 0.5099958609788309, 0.5000802243402851, 0.48564294752712767, 0.49151773694766554, 0.4948140458845617, 0.49589777629848614, 0.4773245919014462, 0.48033648830616926, 0.46032570580288934, 0.46560779291132043, 0.4801967395082556, 0.46931362226728257, 0.45430472585849435, 0.44446468293901886, 0.453079182549647, 0.4386428045558455, 0.44323327799431395, 0.4536723833823952, 0.4494237438033059};


	//------------------------//
	//----- Constructors -----//
	//------------------------//

	/**
	 * Initialises the regional sale market statistics collector
	 *
	 * @param market Reference to the sale or rental market of the region, depending on being called as a constructor
	 *               for this class or as part of the construction of a RegionalRentalMarketStats
	 */
	public HousingMarketStats(HousingMarket market) {
		this.market = market;
		referencePricePerQuality = new double[config.N_QUALITY];
		System.arraycopy(data.HouseSaleMarket.getReferencePricePerQuality(), 0, referencePricePerQuality, 0,
				config.N_QUALITY); // Copies reference prices from data/HouseSaleMarket into referencePricePerQuality
		HPIRecord = new DescriptiveStatistics(config.derivedParams.HPI_RECORD_LENGTH);
	}

    //-------------------//
    //----- Methods -----//
    //-------------------//

    //----- Initialisation methods -----//

    /**
     * Sets initial values for all relevant variables to enforce a controlled first measure for statistics
     */
    public void init() {
        // Set zero initial value for variables computed before market clearing
        nBuyers = 0;
        nSellers = 0;
        nUnsoldNewBuild = 0;
        sumBidPrices = 0.0;
        sumOfferPrices = 0.0;
        offerPrices = new double[nSellers];
        bidPrices = new double[nBuyers];

        // Set zero initial value for persistent variables whose count is computed during market clearing
        nSales = 0;
        nFTBSales = 0;
        nBTLSales = 0;
        sumSoldReferencePrice = 0;
        sumSoldPrice = 0;
        sumMonthsOnMarket = 0; 
        sumSalePricePerQuality = new double[config.N_QUALITY];
        nSalesPerQuality = new int[config.N_QUALITY];
        averageHouseSaleQuality = 0.0;
        moneyToConstructionSector = 0.0;

        // Set initial values for other variables computed after market clearing
        expAvMonthsOnMarket = 0.0; // TODO: Make this initialisation explicit in the paper!
        sumMonthsOnMarketPerQuality = new double[config.N_QUALITY];
        expAvMonthsOnMarketPerQuality  = new double[config.N_QUALITY];
        Arrays.fill(expAvMonthsOnMarketPerQuality, 0.0); // TODO: Make this initialisation explicit in the paper!
        expAvSalePrice = getAvReferencePrice(); // TODO: Make this initialisation explicit in the paper!
        expAvSalePricePerQuality = new double[config.N_QUALITY];
        System.arraycopy(referencePricePerQuality, 0, expAvSalePricePerQuality, 0,
                config.N_QUALITY); // Exponential averaging of prices is initialised from reference prices
        housePriceIndex = 1.0;
        for (int i = 0; i < config.derivedParams.HPI_RECORD_LENGTH; ++i) HPIRecord.addValue(1.0);
        annualHousePriceAppreciation = housePriceAppreciation(1);
        longTermHousePriceAppreciation = housePriceAppreciation(config.HPA_YEARS_TO_CHECK);
    }

    //----- Pre-market-clearing methods -----//

    /**
     * Computes pre-clearing statistics and resets counters to zero
     */
    public void preClearingRecord() {
        // Re-initialise to zero variables to be computed later on, during market clearing, counters
        salesCount = 0;
        ftbSalesCount = 0;
        btlSalesCount = 0;
        sumSoldReferencePriceCount = 0;
        sumSoldPriceCount = 0;
        sumMonthsOnMarketCount = 0;
        sumMonthsOnMarketPerQualityCount = new double[config.N_QUALITY];
        sumSalePricePerQualityCount = new double[config.N_QUALITY];
        nSalesPerQualityCount = new int[config.N_QUALITY];
        moneyToConstructionSectorCount = 0.0;

        // Re-initialise to zero variables computed before market clearing
        nBuyers = market.getBids().size();
        nBTLBuyers = 0;
        nFTBBuyers = 0;
        for (HouseBidderRecord bid: market.getBids()) {
            if (bid.getBidder().behaviour.isPropertyInvestor() && bid.getBidder().getHome() != null) {
                nBTLBuyers++;
            }
            // RUBEN add the number of bids by FTB - this includes BTL that are bidding for their first home
            // as the BTL bids above are only for investment property (investors already have a home)
            if(bid.getBidder().isFirstTimeBuyer()) {
            	nFTBBuyers++;
            }
        }
        nSellers = market.getOffersPQ().size();
        nNewSellers = 0;
        nBTLSellers = 0;
        for (HousingMarketRecord element: market.getOffersPQ()) {
            HouseOfferRecord offer = (HouseOfferRecord)element;
            if (offer.gettInitialListing() == Model.getTime()) {
                nNewSellers++;
            }
            if (offer.getHouse().owner != Model.construction) {
                Household h = (Household) offer.getHouse().owner;
                if (h.behaviour.isPropertyInvestor()) {
                    nBTLSellers++;
                }
            }
        }
        sumBidPrices = 0.0;
        sumOfferPrices = 0.0;
        offerPrices = new double[nSellers];
        bidPrices = new double[nBuyers];


        // Record bid prices and their average
        int i = 0;
        for(HouseBidderRecord bid : market.getBids()) {
            sumBidPrices += bid.getPrice();
            bidPrices[i] = bid.getPrice();
            ++i;
        }

        // Record offer prices, their average, and the number of empty and new houses
        i = 0;
        for(HousingMarketRecord sale : market.getOffersPQ()) {
            sumOfferPrices += sale.getPrice();
            offerPrices[i] = sale.getPrice();
            ++i;
        }
    }

    //----- During-market-clearing methods -----//

    /**
     * This method updates the values of several counters every time a buyer and a seller are matched and the
     * transaction is completed. Note that only counter variables can be modified within this method
     *
     * @param purchase The HouseBidderRecord of the buyer
     * @param sale The HouseOfferRecord of the house being sold
     */
    // TODO: Need to think if this method and recordTransaction can be joined in a single method!
    public void recordSale(HouseBidderRecord purchase, HouseOfferRecord sale) {
        salesCount += 1;
        MortgageAgreement mortgage = purchase.getBidder().mortgageFor(sale.getHouse());
        if(mortgage != null) {
            if(mortgage.isFirstTimeBuyer) {
                ftbSalesCount += 1;
            } else if(mortgage.isBuyToLet) {
                btlSalesCount += 1;
            }
        }
        // TODO: Attention, calls to Model class should be avoided: need to pass transactionRecorder as constructor arg
        if (config.recordTransactions && Model.getTime() >= Model.config.TIME_TO_START_RECORDING) { Model.transactionRecorder.recordSale(purchase, sale, mortgage, market); }
    }

    /**
     * This method updates the values of several counters every time a buyer and a seller are matched and the
     * transaction is completed. Note that only counter variables can be modified within this method
     *
     * @param sale The HouseOfferRecord of the house being sold
     */
    public void recordTransaction(HouseOfferRecord sale) {
        sumMonthsOnMarketCount += Model.getTime() - sale.gettInitialListing();
        sumMonthsOnMarketPerQualityCount[sale.getQuality()] += Model.getTime() - sale.gettInitialListing();
        sumSalePricePerQualityCount[sale.getQuality()] += sale.getPrice();
        nSalesPerQualityCount[sale.getQuality()]++;
        sumSoldReferencePriceCount += referencePricePerQuality[sale.getQuality()];
        sumSoldPriceCount += sale.getPrice();
    }

    // count money outflow to construction sector
    public void recordMoneyOutflowToConstruction(HouseOfferRecord sale) {
    	moneyToConstructionSectorCount += sale.getPrice();
    }
    
    //----- Post-market-clearing methods -----//

    /**
     * This method updates several statistic records after bids have been matched by clearing the market. The
     * computation of the HPI is included here. Note that reference prices from data are used for computing the HPI, and
     * thus the value for t=1 is not 1
     */
    public void postClearingRecord() {
    	// Pass count value obtained during market clearing to persistent variables
    	nSales = salesCount;
    	nFTBSales = ftbSalesCount;
    	nBTLSales = btlSalesCount;
    	sumSoldReferencePrice = sumSoldReferencePriceCount;
    	sumSoldPrice = sumSoldPriceCount;
    	sumMonthsOnMarket = sumMonthsOnMarketCount;
    	moneyToConstructionSector = moneyToConstructionSectorCount;
    	System.arraycopy(sumMonthsOnMarketPerQualityCount, 0, sumMonthsOnMarketPerQuality, 0, config.N_QUALITY);
    	System.arraycopy(nSalesPerQualityCount, 0, nSalesPerQuality, 0, config.N_QUALITY);
    	System.arraycopy(sumSalePricePerQualityCount, 0, sumSalePricePerQuality, 0, config.N_QUALITY);
    	// Compute the rest of variables after market clearing...
    	// ... exponential averages of months in the market and prices per quality band (only if there have been sales)
    	if (nSales > 0) {
    		expAvMonthsOnMarket = config.derivedParams.E*expAvMonthsOnMarket
    				+ (1.0 - config.derivedParams.E)*sumMonthsOnMarket/nSales;
    		expAvSalePrice = config.derivedParams.G*expAvSalePrice
    				+ (1.0 - config.derivedParams.G)*sumSoldPrice/nSales;
    	}
    	for (int q = 0; q < config.N_QUALITY; q++) {
    		if (nSalesPerQuality[q] > 0) {
    			expAvSalePricePerQuality[q] = config.derivedParams.G*expAvSalePricePerQuality[q]
    					+ (1.0 - config.derivedParams.G)*sumSalePricePerQuality[q]/nSalesPerQuality[q];
    			expAvMonthsOnMarketPerQuality[q] = config.derivedParams.E*expAvMonthsOnMarketPerQuality[q]
    					+ (1.0 - config.derivedParams.E)*sumMonthsOnMarketPerQuality[q]/nSalesPerQuality[q];
    		}
    	}
    	// ... current house price index (only if there have been sales)
    	if(nSales > 0) {
    		housePriceIndex = sumSoldPrice/sumSoldReferencePrice;
    	}

    	//TODO TEST: deactivating transactions, but simulating the house price
    	// this basically includes the HPI array initiated by hand above and exchanges it with the calculated one
    	if(Model.getTime() >= config.startTimeDeactivateTransactions) {
    		housePriceIndex = arrayHPI[Model.getTime()-config.TIME_TO_START_RECORDING+1];
    	}
    	// ... HPIRecord with the new house price index value
    	HPIRecord.addValue(housePriceIndex);

    	// ... current house price appreciation values (both annual and long term value)
    	annualHousePriceAppreciation = housePriceAppreciation(1);
    	longTermHousePriceAppreciation = housePriceAppreciation(config.HPA_YEARS_TO_CHECK);
    	// ... relaxation of the price distribution towards the reference price distribution (described in appendix A3)
    	for(int q = 0; q < config.N_QUALITY; q++) {
    		expAvSalePricePerQuality[q] = config.MARKET_AVERAGE_PRICE_DECAY*expAvSalePricePerQuality[q]
    				+ (1.0 - config.MARKET_AVERAGE_PRICE_DECAY)*(housePriceIndex*referencePricePerQuality[q]);
    	}
    	double sum = 0;
    	for(int q = 0; q < config.N_QUALITY; q++) {
    		sum += nSalesPerQuality[q]*(q+1);
    	}
    	if(nSales==0) {averageHouseSaleQuality =0;
    	}else {
    		averageHouseSaleQuality = sum/nSales;}
    	// ...record number of unsold new build houses
    	nUnsoldNewBuild = 0;
    	for(HousingMarketRecord sale : market.getOffersPQ()) {
    		if(((HouseOfferRecord) sale).getHouse().owner == Model.construction) nUnsoldNewBuild++;
    	}
    }

    /**
     * This method computes the annualised appreciation in house price index by comparing the most recent quarter
     * (previous 3 months, to smooth changes) to the quarter nYears years before (full years to avoid seasonal effects)
     * and computing the geometric mean over that period
     *
     * @param nYears Integer with the number of years over which to average house price growth
     * @return Annualised house price appreciation over nYears years
     */
    private double housePriceAppreciation(int nYears) {
        double HPI = (HPIRecord.getElement(config.derivedParams.HPI_RECORD_LENGTH - 1)
                + HPIRecord.getElement(config.derivedParams.HPI_RECORD_LENGTH - 2)
                + HPIRecord.getElement(config.derivedParams.HPI_RECORD_LENGTH - 3));
        double oldHPI = (HPIRecord.getElement(config.derivedParams.HPI_RECORD_LENGTH
                - nYears*config.constants.MONTHS_IN_YEAR - 1)
                + HPIRecord.getElement(config.derivedParams.HPI_RECORD_LENGTH
                - nYears*config.constants.MONTHS_IN_YEAR - 2)
                + HPIRecord.getElement(config.derivedParams.HPI_RECORD_LENGTH
                - nYears*config.constants.MONTHS_IN_YEAR - 3));
        return(Math.pow(HPI/oldHPI, 1.0/nYears) - 1.0);
    }

    /**
     * This method computes the quarter on quarter appreciation in house price index by comparing the most recent
     * quarter (previous 3 months, to smooth changes) to the previous one and computing the percentage change
     *
     * @return Quarter on quarter house price growth
     */
    public double getQoQHousePriceGrowth() {
        double HPI = HPIRecord.getElement(config.derivedParams.getHPIRecordLength() - 1)
                + HPIRecord.getElement(config.derivedParams.getHPIRecordLength() - 2)
                + HPIRecord.getElement(config.derivedParams.getHPIRecordLength() - 3);
        double oldHPI = HPIRecord.getElement(config.derivedParams.getHPIRecordLength() - 4)
                + HPIRecord.getElement(config.derivedParams.getHPIRecordLength() - 5)
                + HPIRecord.getElement(config.derivedParams.getHPIRecordLength() - 6);
        return(100.0*(HPI - oldHPI)/oldHPI);
    }

    //----- Getter/setter methods -----//

    // Note that, for security reasons, getters should never give counter variables, as their value changes during
    // market clearing

    // Getters for variables computed at initialisation
    public double [] getReferencePricePerQuality() { return referencePricePerQuality; }
    public double getReferencePriceForQuality(int quality) { return referencePricePerQuality[quality]; }

    // Getters for variables computed before market clearing
    int getnBuyers() { return nBuyers; }
    int getnBTLBuyers() { return nBTLBuyers; }
    int getnFTBBuyers() { return nFTBBuyers; }
    int getnSellers() { return nSellers; }
    int getnNewSellers() { return nNewSellers; }
    int getnBTLSellers() { return nBTLSellers; }
    int getnUnsoldNewBuild() { return nUnsoldNewBuild; }
    double getSumBidPrices() { return sumBidPrices; }
    double getSumOfferPrices() { return sumOfferPrices; }
    double [] getOfferPrices() { return offerPrices; }
    double [] getBidPrices() { return bidPrices; }

    // Getters for variables computed after market clearing to keep the previous values during the clearing
    int getnSales() { return nSales; }
    int getnFTBSales() { return nFTBSales; }
    int getnBTLSales() { return nBTLSales; }
    double getSumSoldReferencePrice() { return sumSoldReferencePrice; }
    double getSumSoldPrice() { return sumSoldPrice; }
    double getSumMonthsOnMarket() { return sumMonthsOnMarket; }
    public double [] getSumMonthsOnMarketPerQuality() { return sumMonthsOnMarketPerQuality; }
    public double getSumMonthsOnMarketForQuality(int quality) { return sumMonthsOnMarketPerQuality[quality]; }
    public double [] getExpAvMonthsOnMarketPerQuality() { return expAvMonthsOnMarketPerQuality; }
    public double getExpAvMonthsOnMarketForQuality(int quality) { return expAvMonthsOnMarketPerQuality[quality]; }
    public double [] getSumSalePricePerQuality() { return sumSalePricePerQuality; }
    double getSumSalePriceForQuality(int quality) { return sumSalePricePerQuality[quality]; }
    public int [] getnSalesPerQuality() { return nSalesPerQuality; }
    int getnSalesForQuality(int quality) { return nSalesPerQuality[quality]; }
    public double getMoneyToConstructionSector() { return moneyToConstructionSector; }

    // Getters for other variables computed after market clearing
    public double getExpAvMonthsOnMarket() { return expAvMonthsOnMarket; }
    public double [] getExpAvSalePricePerQuality() { return expAvSalePricePerQuality; }
    public double getExpAvSalePriceForQuality(int quality) { return expAvSalePricePerQuality[quality]; }
    double getExpAvSalePrice() { return expAvSalePrice; }
    public double getHPI() { return housePriceIndex; }
    public DescriptiveStatistics getHPIRecord() { return HPIRecord; }
    public double getAnnualHPA() { return annualHousePriceAppreciation; }
    public double getLongTermHPA() {return longTermHousePriceAppreciation; }
    public double getAverageHouseSaleQuality() {return averageHouseSaleQuality; }

    // Getters for derived variables
    double getAvBidPrice() {
        if (nBuyers > 0) {
            return sumBidPrices/nBuyers;
        } else {
            return 0.0;
        }
    }
    double getAvOfferPrice() {
        if (nSellers > 0) {
            return sumOfferPrices/nSellers;
        } else {
            return 0.0;
        }
    }
    double getAvSalePrice() {
        if (nSales > 0) {
            return sumSoldPrice/nSales;
        } else {
            return 0.0;
        }
    }
    // Number of monthly sales that are to first-time buyers
    int getnSalesToFTB() { return nFTBSales; }
    // Number of monthly sales that are to buy-to-let investors
    int getnSalesToBTL() { return nBTLSales; }
    double getAvMonthsOnMarket() {
        if (nSales > 0) {
            return sumMonthsOnMarket/nSales;
        } else {
            return 0.0;
        }
    }
    public double [] getAvSalePricePerQuality() {
        double [] avSalePricePerQuality;
        avSalePricePerQuality = new double[config.N_QUALITY];
        for (int q = 0; q < config.N_QUALITY; q++) {
            if (nSalesPerQuality[q] > 0) {
                avSalePricePerQuality[q] = sumSalePricePerQuality[q]/nSalesPerQuality[q];
            } else {
                avSalePricePerQuality[q] = 0.0;
            }
        }
        return avSalePricePerQuality;
    }
    public double getAvSalePriceForQuality(int quality) {
        if (nSalesPerQuality[quality] > 0) {
            return sumSalePricePerQuality[quality]/nSalesPerQuality[quality];
        } else {
            return 0.0;
        }
    }
    /**
     * Computes the best quality of house that a buyer could expect to get for a given price. If return value is -1,
     * the buyer can't afford even lowest quality house.
     *
     * @param price Price the buyer is ready to pay
     */
    public int getMaxQualityForPrice(double price) {
        int q = config.N_QUALITY - 1;
        while(q >= 0 && getExpAvSalePriceForQuality(q) > price) --q;
        return q;
    }
    private double getAvReferencePrice() {
        double avReferencePrice = 0.0;
        for (double price: referencePricePerQuality) {
            avReferencePrice += price;
        }
        return avReferencePrice/referencePricePerQuality.length;
    }
}

