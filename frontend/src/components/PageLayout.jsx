// È un componente "puro" di presentazione. Riceve dati (props) e li disegna.
// Props:
// - children: È il contenuto vero e proprio della pagina.
// - title/subtitle: I testi in alto a sinistra.
// - extra: Uno spazio opzionale a destra del titolo.
const PageLayout = ({ children, title, subtitle, extra }) => (

  // CONTAINER PRINCIPALE
  <div className="max-w-6xl mx-auto p-6 pt-24 min-h-screen">

    {/* HEADER DELLA PAGINA */}
    <div className="flex items-center justify-between mb-8 px-2">

      {/* SEZIONE TITOLI */}  
      <div>
        <h2 className="text-3xl font-black text-slate-900 tracking-tight italic">
          {title}
        </h2>

        {/* Rendering Condizionale: Il sottotitolo appare solo se viene passato */}
        {subtitle && (
          <span className="block text-sm font-medium text-slate-400 mt-1 uppercase tracking-widest">
            {subtitle}
          </span>
        )}
      </div>

      {/* SEZIONE EXTRA (es. Bottoni) */}
      {extra}
    </div>

    {/* CONTENUTO VERO E PROPRIO */}
    {/* Qui React inietta tutto quello che c'è dentro <PageLayout>...</PageLayout> */}
    {children}
  </div>
);

export default PageLayout;