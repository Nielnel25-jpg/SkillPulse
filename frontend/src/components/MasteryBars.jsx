/** Horizontal mastery bars. Items: { skillId, skillName, percent }. Colour: <50 weak, <75 mid, else strong. */
export default function MasteryBars({ items }) {
  if (!items?.length) return <p className="muted">No data yet.</p>;
  return (
    <div>
      {items.map((s) => {
        const cls = s.percent < 50 ? 'bar-weak' : s.percent < 75 ? 'bar-mid' : 'bar-strong';
        return (
          <div className="bar-row" key={s.skillId}>
            <span className="bar-label" title={s.skillName}>{s.skillName}</span>
            <div className="bar-track" role="progressbar" aria-valuenow={s.percent} aria-valuemin={0} aria-valuemax={100}>
              <div className={`bar-fill ${cls}`} style={{ width: `${s.percent}%` }} />
            </div>
            <span className="bar-pct">{s.percent}%</span>
          </div>
        );
      })}
    </div>
  );
}
