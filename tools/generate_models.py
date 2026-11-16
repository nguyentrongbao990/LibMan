"""Generate only the exact entity fields declared in SPEC section 3."""
from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
spec = (root / 'docs/SPEC.md').read_text(encoding='utf-8')
rows = spec.split('## 3. Entity')[1].split('## 4. CSDL')[0]
for name, definition in re.findall(r'^\| (\w+1)(?: \(abstract\))? \| (.*?) \|$', rows, re.M):
    base = re.search(r'extends (\w+1)', definition)
    fields = re.findall(r'(\w+):([\w\[\]]+)', definition)
    abstract = 'abstract ' if name == 'Staff1' else ''
    inheritance = ' extends ' + base.group(1) if base else ''
    lines = ['package model;', '']
    if any(t == 'LocalDate' for _, t in fields): lines += ['import java.time.LocalDate;', '']
    if any(t == 'BigDecimal' for _, t in fields): lines += ['import java.math.BigDecimal;', '']
    lines += [f'public {abstract}class {name}{inheritance} {{']
    lines += [f'    private {t} {n};' for n,t in fields]
    lines += ['', f'    public {name}() {{}}']
    for n,t in fields:
        cap = n[0].upper()+n[1:]
        lines += ['', f'    public {t} get{cap}() {{ return {n}; }}', f'    public void set{cap}({t} {n}) {{ this.{n} = {n}; }}']
    lines += ['}', '']
    dest = root / f'src/main/java/model/{name}.java'
    dest.parent.mkdir(parents=True, exist_ok=True)
    dest.write_text('\n'.join(lines), encoding='utf-8')
